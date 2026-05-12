import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    public static final AtomicInteger counterForId = new AtomicInteger(1);
    private final int id;
    private final List<Ingredient> ingredientsThatAreRequired; 
    private final EnumMap<Ingredient, Boolean> collectedIngredients;

    public Order(List<Ingredient> recipe) {
        this.id = counterForId.getAndIncrement();
        this.ingredientsThatAreRequired = new ArrayList<>(recipe);
        this.collectedIngredients = new EnumMap<>(Ingredient.class);

        for (Ingredient ing : recipe) {
            collectedIngredients.put(ing, false);
        }
    }

    public int getId() {
        return id;
    }


    public synchronized List<Ingredient> getMissingIngredients() {
        List<Ingredient> missingIng = new ArrayList<>();

        for (var curr : collectedIngredients.entrySet()) {
            if (!curr.getValue()) {
                missingIng.add(curr.getKey());
            }
        }

        return missingIng;
    }


    public synchronized void receiveIngredient(Ingredient ing) {
        if (collectedIngredients.containsKey(ing)) {
            collectedIngredients.put(ing, true);
        }
    }


    public synchronized int getPrepTime() {
        int total = 0;

        for (var curr : collectedIngredients.entrySet()) {
            if (curr.getValue()){
                total += curr.getKey().getPreparationTime();
            }
        }
        
        return total;
    }


    public synchronized boolean isReady() {
        for (boolean value : collectedIngredients.values()) {
            if (!value){
                return false;
            }
        }

        return true;
    }


    public List<Ingredient> getRequiredIngredients() {
        return ingredientsThatAreRequired;
    }
}
