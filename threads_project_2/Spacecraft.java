package systems;

//javac -d . *.java
//java systems.SpaceFleet

//Grading key: PowerSupplier (4) + SpacecraftData (3) + OxygenGenerator (8) + secondary (10) + Spacecraft (7) + Analyzer (8) + SpaceFleet (10)
//4+3+8+10+7+8+10=50

import systems.primary.OxygenGenerator;
import systems.secondary.CommunicationHandler;
import systems.secondary.PropulsionController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class PowerSupplier {
    final int INITIAL_ENERGY = 10;
    int energy;
    public PowerSupplier() {
        energy = INITIAL_ENERGY;
    }
    public synchronized boolean consumePower(int value) {
        if (energy >= value) {
            energy -= value;
            return true;
        }
        return false;
    }
    public synchronized void addPower(int value) { energy += value; }
}

class SpacecraftData {
    public AtomicBoolean isAlive = new AtomicBoolean(true);
}

public class Spacecraft implements Callable<Boolean> {
    SpacecraftData sd;
    PowerSupplier ps;
    public String name;
    public Spacecraft(String name) {
        ps = new PowerSupplier();
        ps.addPower(ThreadLocalRandom.current().nextInt(100, 150+1));
        sd = new SpacecraftData();
        this.name = name;
    }
    public Boolean call() {
        ScheduledExecutorService es = Executors.newScheduledThreadPool(3);
        es.scheduleWithFixedDelay(new OxygenGenerator(this), 1, 1, TimeUnit.SECONDS);
        es.scheduleWithFixedDelay(new CommunicationHandler(this), 2, 2, TimeUnit.SECONDS);
        es.scheduleWithFixedDelay(new PropulsionController(this), 2, 2, TimeUnit.SECONDS);
        
        try {
            synchronized (this) { wait(10000, 0); }
        } catch (InterruptedException e) {}
        es.shutdownNow();
        return sd.isAlive.get();
    }
    public boolean consumePower(int value) { return ps.consumePower(value); }
    public boolean getIsAlive() { return sd.isAlive.get(); }
    public void setNotAlive() {
        sd.isAlive.set(false);
        synchronized (this) { notify(); }
    }
}

class Analyzer implements Callable<Integer> {
    BlockingQueue<Future<Boolean>> futures;
    boolean isAlive;
    public Analyzer(BlockingQueue<Future<Boolean>> futures, boolean isAlive) {
        this.futures = futures;
        this.isAlive = isAlive;
    }
    public Integer call() {
        int count = 0;
        while (true) {
            synchronized (futures) {
                Future<Boolean> f = futures.poll();
                if (f == null) break;
                try {
                    if (f.get() == isAlive) count++;
                    else futures.add(f);
                } catch (InterruptedException|ExecutionException e) {}
            }
        }
        return count;
    }
}

class SpaceFleet {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        BlockingQueue<Future<Boolean>> futures = new ArrayBlockingQueue<>(10);
        for (int i = 0; i < 10; i++)
            futures.add(es.submit(new Spacecraft("Endymion-" + i)));
        es.shutdown();
        try {
            es.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        es.shutdownNow();
        es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(new Analyzer(futures, true));
        Future<Integer> f2 = es.submit(new Analyzer(futures, false));
        es.shutdown();
        try {
            es.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        es.shutdownNow();
        try {
            System.out.println("Count of survivors: " + f1.get());
            System.out.println("Count of dead: " + f2.get());
        } catch (InterruptedException|ExecutionException e) {}
    }
}