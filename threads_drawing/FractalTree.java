import java.awt.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class FractalTree extends Canvas {
    private static boolean slowMode;
    private static final int SIZE_QUEUE_LARGEST = 1000;
    private static final BlockingQueue<DrawingTheLine> linesDrawQueue = new ArrayBlockingQueue<>(SIZE_QUEUE_LARGEST);
    private static final int THREAD_CNT_CAPPED = 128;
    private static ExecutorService taskExecutorService;
    private static final AtomicInteger runningTaskCnt = new AtomicInteger(0);
    private static final Object waitNotifyLock = new Object();

    private static class DrawingTheLine {
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final Color color;

        DrawingTheLine(int x1, int y1, int x2, int y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

    private static void sleepIfSlowMode() {
        if (!slowMode) {
            return;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private boolean enqueueTheLine(int x1, int y1, int x2, int y2, Color color) {
        try {
            linesDrawQueue.put(new DrawingTheLine(x1, y1, x2, y2, color));
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static void taskFinished() {
        runningTaskCnt.decrementAndGet();
        synchronized (waitNotifyLock) {
            waitNotifyLock.notifyAll();
        }
    }

    public void makeFractalTree(Graphics g, int x, int y, int angle, int height) {
        sleepIfSlowMode();

        if (height == 0) {
            return;
        }

        int x2 = x + (int) (Math.cos(Math.toRadians(angle)) * height * 8);
        int y2 = y + (int) (Math.sin(Math.toRadians(angle)) * height * 8);
        Color color;
        if (height < 5) {
            color = Color.GREEN;
        } else {
            color = Color.BLACK;
        }

        if (!enqueueTheLine(x, y, x2, y2, color)) {
            return;
        }

        int nextHeight = height - 1;
        if (nextHeight <= 0) {
            return;
        }

        runningTaskCnt.incrementAndGet();
        taskExecutorService.submit(() -> {
            try {
                // I am passing null here professor because I wanted to keep the original structure that you gave and I am not using the graphics param here
                makeFractalTree(null, x2, y2, angle - 20, nextHeight);
            } finally {
                taskFinished();
            }
        });

        makeFractalTree(null, x2, y2, angle + 20, nextHeight);
    }

    @Override
    public void paint(Graphics g) {
        while (true) {
            try {
                DrawingTheLine segment = linesDrawQueue.take();
                g.setColor(segment.color);
                g.drawLine(segment.x1, segment.y1, segment.x2, segment.y2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void main(String[] args) {
        /* Parse args */
        slowMode = args.length != 0 && Boolean.parseBoolean(args[0]);
        /* Initialize graphical elements and EDT */
        FractalTree tree = new FractalTree();
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.add(tree);
        frame.setVisible(true);

        taskExecutorService = Executors.newFixedThreadPool(THREAD_CNT_CAPPED);

        runningTaskCnt.incrementAndGet();
        taskExecutorService.submit(() -> {
            try {
                tree.makeFractalTree(null, 390, 480, -90, 10);
            } finally {
                taskFinished();
            }
        });

        synchronized (waitNotifyLock) {
            while (runningTaskCnt.get() > 0) {
                try {
                    waitNotifyLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        taskExecutorService.shutdown();
        /* Log success as last step */
        System.out.println("Main has finished");
    }
}
