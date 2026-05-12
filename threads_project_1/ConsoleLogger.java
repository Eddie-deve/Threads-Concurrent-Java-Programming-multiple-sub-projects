import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConsoleLogger implements Runnable {
    BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
    public void log(String s) {
        try { queue.put(s); } catch (InterruptedException e) {}
    }
    public void run() {
        try {
            while (true) {
                String s = queue.take();
                System.out.println(s);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.println("Logger is shutting down.");
        }
    }
}
