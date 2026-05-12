import java.util.Random;

class Sheep extends Thread {
    private int x;
    private int y;

    private final Farm farm;

    private final Random random = new Random();

    public Sheep(Farm farm, String name, int x, int y) {

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

            int dx = determineXDirection();
            int dy = determineYDirection();

            if (dx == 0 && dy == 0) {

                if (random.nextBoolean()) {
                    dx = randomDirectionWithoutZero();
                }
                else {
                    dy = randomDirectionWithoutZero();
                }
            }

            int newX = x + dx;
            int newY = y + dy;

            if (!validPosition(newX, newY)) {
                continue;
            }

            move(newX, newY);
        }
    }

    private int determineXDirection() {

        boolean leftDog = dogExists(x - 1, y);
        boolean rightDog = dogExists(x + 1, y);

        if (leftDog && !rightDog) {
            return 1;
        }

        if (rightDog && !leftDog) {
            return -1;
        }

        return random.nextInt(3) - 1;
    }

    private int determineYDirection() {

        boolean upDog = dogExists(x, y - 1);
        boolean downDog = dogExists(x, y + 1);

        if (upDog && !downDog) {
            return 1;
        }

        if (downDog && !upDog) {
            return -1;
        }

        return random.nextInt(3) - 1;
    }

    private int randomDirectionWithoutZero() {

        if (random.nextBoolean()) {
            return 1;
        }

        return -1;
    }

    private boolean dogExists(int x, int y) {

        if (!insideFarm(x, y)) {
            return false;
        }

        return farm.get(x, y).occupant instanceof Dog;
    }

    private boolean validPosition(int x, int y) {

        if (!insideFarm(x, y)) {
            return false;
        }

        Cell cell = farm.get(x, y);

        return !cell.isWall;
    }

    private boolean insideFarm(int x, int y) {

        return x >= 0 &&
               y >= 0 &&
               x < farm.getWidth() &&
               y < farm.getHeight();
    }

    private void move(int newX, int newY) {

        Cell currentCell = farm.get(x, y);
        Cell targetCell = farm.get(newX, newY);

        lockCells(currentCell, targetCell);

        try {

            if (!targetCell.isFree() && !targetCell.isGate) {
                return;
            }

            currentCell.occupant = null;

            if (targetCell.isGate) {

                farm.sheepEscaped(
                        getName(),
                        newX,
                        newY
                );

                return;
            }

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