import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//javac Simulation.java
//java Simulation

//Grading key: Simulation (5) + Petting Zoo (15) + Guest (15) + Animal (15) + Food Producer (5) + Doctor (5)
//5+15+15+15+5+5=60

class Guest implements Runnable {
    final PettingZoo pz;
    AtomicInteger food = new AtomicInteger(0);
    volatile Animal currentAnimal;
    final int patienceLevel;
    final int id;
    public Guest(PettingZoo pz, int id) {
        this.pz = pz;
        this.id = id;
        patienceLevel = ThreadLocalRandom.current().nextInt(100, 200+1);
    }
    public void run() {
        pz.enter(this);
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 120+1));
        } catch (InterruptedException e) {}
        food.set(pz.buyPetFood(patienceLevel));
        if (food.get() <= 0) {
            System.out.println("I have nothing to do here");
        } else {
            while (pz.isOpen() && food.get() > 0) {
                standStill();
                synchronized (this) {
                    currentAnimal = pz.getRandomAnimal();
                    if (currentAnimal != null) currentAnimal = currentAnimal.tryToAttach(this);
                }
                while (pz.isOpen() && currentAnimal != null && food.get() > 0) {
                    if (shouldStay()) {
                        printPet();
                    } else {
                        Animal a = currentAnimal;
                        if (a != null) a.detach(this);
                    }
                }
            }
        }
        pz.exit(this);        
    }
    public boolean shouldStay() { return ThreadLocalRandom.current().nextBoolean(); }
    public synchronized void printPet() {
        if (currentAnimal != null)
            System.out.println("Guest #" + id + " petting " + currentAnimal);
    }
    public void standStill() {
        try { Thread.sleep(50); } catch (InterruptedException e) {}
    }
    public boolean feed() {
        return food.getAndUpdate((x) -> Math.max(0, x-1)) != 0;
    }
    public synchronized void leave(Animal a) {
        if (a == currentAnimal) currentAnimal = null;
    }
}

class Animal implements Runnable {
    final String name;
    final PettingZoo pz;
    List<Guest> curGuests = new Vector<>();
    public Animal(PettingZoo pz, String name) { this.pz = pz; this.name = name; }
    boolean shouldMove() {
        return ThreadLocalRandom.current().nextBoolean();
    }
    boolean gotSick() {
        return ThreadLocalRandom.current().nextBoolean();
    }
    void remainInPlace() {
        try { Thread.sleep(ThreadLocalRandom.current().nextInt(70, 210+1)); }
        catch (InterruptedException e) {}
    }
    Animal tryToAttach(Guest g) {
        synchronized (curGuests) {
            if (curGuests.contains(g)) return null;
            curGuests.add(g);
            return this;
        }
    }
    void detach(Guest g) {
        curGuests.remove(g);
        g.leave(this);
    }
    public Guest getRandomGuest() {
        synchronized (curGuests) {
            return curGuests.size() == 0 ? null : curGuests.get(ThreadLocalRandom.current().nextInt(0, curGuests.size()));
        }
    }
    void cured() {
        synchronized (this) { notify(); }
    }
    void detachAll() {
        synchronized (curGuests) {
            while (curGuests.size() != 0) {
                detach(curGuests.get(0));
            }
        }
    }
    public boolean getFood() {
        Guest g = getRandomGuest();
        return g != null && g.feed();
    }
    public void run() {
        while (pz.isOpen()) {
            boolean gotFood = getFood();
            if (!gotFood && shouldMove()) {
                detachAll();
                pz.move(this);
                remainInPlace();
            } else if (gotFood && gotSick()) {
                detachAll();
                pz.markSick(this);
                try { synchronized (this) { wait(); } } catch (InterruptedException e) {}
            }
        }
    }
    public String toString() { return name; }

}

class Goat extends Animal
{
    public Goat(PettingZoo pz, int id) { super(pz, "Goat #" + id); }
}

class Bunny extends Animal
{
    public Bunny(PettingZoo pz, int id) { super(pz, "Bunny #" + id); }
}

class GuineaPig extends Animal
{
    public GuineaPig(PettingZoo pz, int id) { super(pz, "GuineaPig #" + id); }
}

class FoodProducer implements Runnable {
    final PettingZoo pz;
    FoodProducer(PettingZoo pz) { this.pz = pz; }
    public void run() {
        while (pz.isOpen()) {
            pz.addFood(createFood());
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
    }
    int createFood() { return ThreadLocalRandom.current().nextInt(1, 10+1); }
}

class Doctor implements Runnable {
    BlockingQueue<Animal> sickAnimals = new ArrayBlockingQueue<>(3+5+10);
    final PettingZoo pz;
    Doctor(PettingZoo pz) { this.pz = pz; }
    public void addSickAnimal(Animal a) {
        sickAnimals.add(a);
    }
    public void run() {
        while (pz.isOpen()) {
            try {
                Animal a = sickAnimals.poll(1, TimeUnit.SECONDS);
                if (a != null) cure(a);
            } catch (InterruptedException e) {}
        }
    }
    public void cure(Animal a) {
        a.cured();
    }
}

class PettingZoo implements Runnable {
    BlockingQueue<Integer> foodSupply = new ArrayBlockingQueue<>(10);
    List<Guest> guests = new Vector<>();
    ExecutorService es = Executors.newFixedThreadPool(1+1+3+5+10);
    List<Animal> stable = new Vector<>();
    List<Animal> runway = new Vector<>();
    AtomicBoolean isOpen = new AtomicBoolean(true);
    Doctor doctor;
    public PettingZoo() { doctor = new Doctor(this); }
    public void enter(Guest g) { guests.add(g); }
    public void exit(Guest g) { guests.remove(g); }
    public void addFood(int food) {
        try {
            foodSupply.offer(food, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}
    }
    public int buyPetFood(int patienceLevel)
    {
        Integer f = 0;
        try {
            f = foodSupply.poll(patienceLevel, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}
        return f == null ? 0 : f;
    }
    public Animal getRandomAnimal() {
        synchronized (runway) {
            return runway.size() == 0 ? null : runway.get(ThreadLocalRandom.current().nextInt(0, runway.size()));
        }
    }
    public void move(Animal a) {
        if (runway.contains(a)) {
            runway.remove(a);
            stable.add(a);
        } else {
            stable.remove(a);
            runway.add(a);
        }
    }
    public void markSick(Animal a) {
        doctor.addSickAnimal(a);
    }
    public void run() {
        es.submit(new FoodProducer(this));        
        es.submit(doctor);
        for (int i = 0; i < 3; i++) { 
            Goat g = new Goat(this, i);
            stable.add(g);
            es.submit(g);
        }
        for (int i = 0; i < 5; i++) {
            Bunny b = new Bunny(this, i);
            stable.add(b);
            es.submit(b);
        }
        for (int i = 0; i < 10; i++) {
            GuineaPig gp = new GuineaPig(this, i);
            stable.add(gp);
            es.submit(gp);
        }
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        while (isOpen.get() && guests.size() != 0) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
        isOpen.set(false);
    }
    public boolean isOpen() { return isOpen.get(); }
    public void close() {
        isOpen.set(false);
        es.shutdown();
        try { es.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        es.shutdownNow();
    }
}

public class Simulation {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(1+300);
        PettingZoo pz = new PettingZoo();
        es.submit(pz);
        for (int i = 0; i < 1000; i++) {
            es.submit(new Guest(pz, i));
        }
        es.shutdown();
        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        pz.close();
        es.shutdownNow();
        System.out.println("Simulation has finished");
    }
}