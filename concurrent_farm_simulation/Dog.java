import java.util.Random;

class Dog extends Thread {

    private int x;
    private int y;

    private final Farm farm;

    private final Random random = new Random();

    public Dog(Farm farm, String name, int x, int y) {

        super(name);

        this.farm = farm;

        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {

        while (farm.isRunning()) {

            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
                break;
            }

            int dx = randomDirectionWithoutZero();
            int dy = randomDirectionWithoutZero();

            int newX = x + dx;
            int newY = y + dy;

            if (!validPosition(newX, newY)) {
                continue;
            }

            if (farm.isMidZone(newX, newY)) {
                continue;
            }

            move(newX, newY);
        }
    }

    private int randomDirectionWithoutZero() {

        if (random.nextBoolean()) {
            return 1;
        }

        return -1;
    }

    private boolean validPosition(int x, int y) {

        return x >= 0 &&
               y >= 0 &&
               x < farm.getWidth() &&
               y < farm.getHeight() &&
               !farm.get(x, y).isWall;
    }

    private void move(int newX, int newY) {

        Cell currentCell = farm.get(x, y);
        Cell targetCell = farm.get(newX, newY);

        lockCells(currentCell, targetCell);

        try {

            if (!targetCell.isFree()) {
                return;
            }

            currentCell.occupant = null;

            targetCell.occupant = this;

            x = newX;
            y = newY;
        }
        finally {

            currentCell.lock.unlock();
            targetCell.lock.unlock();
        }
    }

    private void lockCells(Cell first, Cell second) {

        Cell a = first;
        Cell b = second;

        if (System.identityHashCode(a) > System.identityHashCode(b)) {
            a = second;
            b = first;
        }

        a.lock.lock();
        b.lock.lock();
    }

    @Override
    public String toString() {
        return getName();
    }
}