import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SheepMain {

    public static void main(String[] args) {

        int width = 14;
        int height = 14;

        int sheepCount = 10;
        int dogCount = 5;

        Farm farm = new Farm(width, height);

        Random random = new Random();

        List<Thread> allThreads = new ArrayList<>();

        int middleXStart = ((width - 2) / 3) + 1;
        int middleXEnd = middleXStart + ((width - 2) / 3) - 1;

        int middleYStart = ((height - 2) / 3) + 1;
        int middleYEnd = middleYStart + ((height - 2) / 3) - 1;

        for (int i = 0; i < sheepCount; i++) {

            int x;
            int y;

            do {

                x = random.nextInt(
                        middleXEnd - middleXStart + 1
                ) + middleXStart;

                y = random.nextInt(
                        middleYEnd - middleYStart + 1
                ) + middleYStart;

            }
            while (!farm.get(x, y).isFree());

            Sheep sheep =
                    new Sheep(
                            farm,
                            String.valueOf((char) ('A' + i)),
                            x,
                            y
                    );

            farm.get(x, y).occupant = sheep;

            allThreads.add(sheep);

            sheep.start();
        }

        for (int i = 0; i < dogCount; i++) {

            int x;
            int y;

            do {

                x = random.nextInt(width - 2) + 1;
                y = random.nextInt(height - 2) + 1;

            }
            while (
                    !farm.get(x, y).isFree() ||
                    farm.isMidZone(x, y)
            );

            Dog dog =
                    new Dog(
                            farm,
                            String.valueOf(i),
                            x,
                            y
                    );

            farm.get(x, y).occupant = dog;

            allThreads.add(dog);

            dog.start();
        }

        while (farm.isRunning()) {

            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                break;
            }

            if (!farm.isRunning()) {
                break;
            }

            System.out.print("\033[H\033[2J");
            System.out.flush();

            System.out.print("\u001B[0;0H");

            farm.print();
        }

        for (Thread t : allThreads) {

            try {
                t.join();
            }
            catch (InterruptedException ignored) {
            }
        }

        System.out.println("Simulation ended.");
    }
}