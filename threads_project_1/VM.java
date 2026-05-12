import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class VM {
  static class Process {
    // TODO create variables
    static AtomicInteger nextIndex = new AtomicInteger(0);
    int index;
    AtomicBoolean completed = new AtomicBoolean(false);

    // TODO write a constructor
    Process() {
      index = nextIndex.getAndIncrement();
    }

    // TODO write the getter methods mentioned in the task description
    boolean getCompleted() { return completed.get(); }

    // TODO method simulating the completion of a task
    public void dispatch() {
      int time = ThreadLocalRandom.current().nextInt(100, 400+1);
      work(time);
      completed.set(true);
      //System.out.println("Process #" + index + " ran successfully");
      consoleLogger.log("Process #" + index + " ran successfully");
    }

    public static void work(int ms) {
      try {
        Thread.sleep(ms);
      } catch (InterruptedException e) { e.printStackTrace(); }
    }
  }

  private static BlockingQueue<Process> queue = new ArrayBlockingQueue<>(8);

  //TODO Task 2: create atomic variables
  static Thread user, scheduler;
  static AtomicBoolean isUserOn = new AtomicBoolean(true),
    isSchedulerOn = new AtomicBoolean(true);

  private static ConsoleLogger consoleLogger = new ConsoleLogger();

  public static void main(String[] args) {
    //part1();
    //part2();
    part3();
  }

  // TODO Task 1
  public static void part1() {    
    user = startUser1();
    scheduler = startScheduler1();
    user.start();
    scheduler.start();
    try {
      user.join();
      scheduler.join();
    } catch (InterruptedException e) {}
  }

  // TODO Task 2
  public static void part2() {
    user = startUser2();
    scheduler = startScheduler2();
    user.start();
    scheduler.start();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {}
    isUserOn.set(false);
    isSchedulerOn.set(false);
    gracefulShutdown(user, scheduler);
  }

  // TODO Task 3
  public static void part3() {
    ExecutorService es = Executors.newFixedThreadPool(3);
    es.submit(consoleLogger);
    es.submit(startUser2());
    es.submit(startScheduler2());
    try { Thread.sleep(5000); } catch (InterruptedException e) {}
    isUserOn.set(false);
    isSchedulerOn.set(false);
    es.shutdown();
    try {
      es.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {}
    es.shutdownNow();
    System.out.println("The system is down.");
  }

  // TODO Task 2
  private static void gracefulShutdown(Thread user, Thread scheduler) {
    System.out.println("Shutdown is in progress.");
    try {
      user.join();
      scheduler.join();
    } catch (InterruptedException e) {}
    System.out.println("All proccesses shut down gracefully.");
  }

  // TODO Task 1
  private static Thread startUser1() {
    return new Thread(() -> {
      while (true) {
        try {
          queue.put(new Process());
          Thread.sleep(50);
        } catch (InterruptedException e) {}
      }
    });
  }

  // TODO Task 1
  private static Thread startScheduler1() {
    return new Thread(() -> {
      while (true) {
        try {
          queue.take().dispatch();
        } catch(InterruptedException e) {}
      }
    });
  }

  // TODO Task 2
  private static Thread startUser2() {
    return new Thread(() -> {
      while (isUserOn.get()) {
        try {
          if (queue.offer(new Process(), 100, TimeUnit.MILLISECONDS))
            Thread.sleep(50);
        } catch (InterruptedException e) {}
      }
    });

  }

  // TODO Task 2
  private static Thread startScheduler2() {
    return new Thread(() -> {
      while (isSchedulerOn.get()) {
        try {
          Process p = queue.poll(100, TimeUnit.MILLISECONDS);
          if (p != null) p.dispatch();
        } catch(InterruptedException e) {}
      }
    });
  }
}
