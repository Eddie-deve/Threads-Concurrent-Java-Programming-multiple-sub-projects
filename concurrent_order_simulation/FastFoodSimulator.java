import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class FastFoodSimulator {
    private static final int NUMBER_OF_CUSTOMERS = 30;
    private static final int NUMBER_OF_KITCHEN_STAFF = 4;
    private static final int CUSTOMER_LINE_SIZE = 5;
    private static final int STARTER_INVENTORY_AMOUNT_PART1 = 10;
    private static final int STARTER_INVENTORY_AMOUNT_PART2 = 3;
    private static final int GENERIC_WAIT_TIME_MS = 300;
    private static final int CUSTOMER_ORDER_PATIENCE_MS = 500;
    private static final int SIMULATION_MAX_DURATION_MS = 8000;
    private static final int RESTOCK_AMOUNT_MIN = 5;
    private static final int RESTOCK_AMOUNT_MAX = 8;
    private static final int RESTOCK_TIME_MIN_MS = 200;
    private static final int RESTOCK_TIME_MAX_MS = 600;

    private final EnumMap<Ingredient, Integer> inventory = new EnumMap<>(Ingredient.class);
    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(CUSTOMER_LINE_SIZE);
    private final Map<Ingredient, Object> restockingHelp = new EnumMap<>(Ingredient.class);
    private final Map<Ingredient, Boolean> immediatelyRestockingNow = new EnumMap<>(Ingredient.class);
    private final AtomicInteger ordersWhichAreSucces = new AtomicInteger(0);
    private final AtomicInteger ordersThatFailed = new AtomicInteger(0);
    private volatile boolean isRunning = true;

    public static void main(String[] args) {
        new FastFoodSimulator().simulationStartedRunning();
    }

    private void setup(boolean part2) {
        int startAmount = part2 ? STARTER_INVENTORY_AMOUNT_PART2 : STARTER_INVENTORY_AMOUNT_PART1;

        for (Ingredient i : Ingredient.values()) {
            inventory.put(i, startAmount);
            restockingHelp.put(i, new Object());
            immediatelyRestockingNow.put(i, false);
        }
    }

    public void simulationStartedRunning() {
        System.out.println("\n... first");
        setup(false);
        runPart(false);

        System.out.println("\n... second");
        setup(true);
        runPart(true);
    }

    private void runPart(boolean part2) {
        ExecutorService kitchenPool = Executors.newFixedThreadPool(NUMBER_OF_KITCHEN_STAFF);
        ExecutorService poolForCostumers = Executors.newFixedThreadPool(CUSTOMER_LINE_SIZE);
        isRunning = true;

        ordersWhichAreSucces.set(0);
        ordersThatFailed.set(0);
        Order.counterForId.set(1);

        for (int i = 0; i < NUMBER_OF_KITCHEN_STAFF; i++) {
            int workerId = i + 1;
            kitchenPool.submit(() -> kitchenWorkerAction(workerId, part2));
        }

        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            int customerId = i + 1;
            poolForCostumers.submit(() -> customerAction(customerId));
        }

        try {
            poolForCostumers.shutdown();
            poolForCostumers.awaitTermination(SIMULATION_MAX_DURATION_MS, TimeUnit.MILLISECONDS);
            isRunning = false;
            kitchenPool.shutdown();
            kitchenPool.awaitTermination(GENERIC_WAIT_TIME_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {}

        kitchenPool.shutdownNow();
        System.out.println("Completed orders: " + ordersWhichAreSucces.get());
        System.out.println("Failed orders: " + ordersThatFailed.get());
    }

    private void kitchenWorkerAction(int workerId, boolean part2) {
        while (ordersWhichAreSucces.get() + ordersThatFailed.get() < NUMBER_OF_CUSTOMERS) {
            try {
                Order order = orderQueue.poll(GENERIC_WAIT_TIME_MS, TimeUnit.MILLISECONDS);
                if (order == null) {
                    continue;
                }
                System.out.println("starting to prepare order #" + order.getId());

                for (Ingredient ingr : order.getRequiredIngredients()) {
                    synchronized (inventory) {
                        int stock = inventory.get(ingr);
                        if (part2 && stock <= 0) {
                            restockIngredient(ingr);
                            waitForRestock(ingr);
                            stock = inventory.get(ingr);
                        }

                        inventory.put(ingr, stock - 1);
                    }
                    order.receiveIngredient(ingr);
                }

                if (order.isReady()) {
                    Thread.sleep(order.getPrepTime());

                    System.out.println("Order #" + order.getId() + " is prepared and ready to be served");
                    ordersWhichAreSucces.incrementAndGet();

                    synchronized (order) {
                        order.notifyAll();
                    }
                } else {
                    ordersThatFailed.incrementAndGet();
                    System.out.println("Order #" + order.getId() + "is screwed up, that's a failed order");
                }
            } 
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    private void customerAction(int customerId) {
        try {
            Order order = new Order(Recipes.getRandomRecipe());
            boolean added = orderQueue.offer(order, CUSTOMER_ORDER_PATIENCE_MS, TimeUnit.MILLISECONDS);
            if (!added) {
                System.out.println("Customer #" + customerId + " left, queue full!");
                ordersThatFailed.incrementAndGet();
                return;
            }
            synchronized (order) {
                order.wait();
            }

            System.out.println("Customer received order #" + order.getId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void restockIngredient(Ingredient ingr) throws InterruptedException {
        synchronized (restockingHelp.get(ingr)) {
            if (immediatelyRestockingNow.get(ingr)){
                return;
            }

            immediatelyRestockingNow.put(ingr, true);
        }

        int amountOfRestock = ThreadLocalRandom.current().nextInt(RESTOCK_AMOUNT_MIN, RESTOCK_AMOUNT_MAX + 1);
        int restockTime = ThreadLocalRandom.current().nextInt(RESTOCK_TIME_MIN_MS, RESTOCK_TIME_MAX_MS + 1);
        
        Thread.sleep(restockTime);

        synchronized (inventory) {
            inventory.put(ingr, inventory.get(ingr) + amountOfRestock);
            System.out.println("Restocked " + amountOfRestock + " " + ingr);

            inventory.notifyAll();
        }

        synchronized (restockingHelp.get(ingr)) {
            immediatelyRestockingNow.put(ingr, false);
        }
    }

    private void waitForRestock(Ingredient ingr) throws InterruptedException {
        synchronized (inventory) {
            while (inventory.get(ingr) <= 0) {
                inventory.wait();
            }
        }

    }
}
