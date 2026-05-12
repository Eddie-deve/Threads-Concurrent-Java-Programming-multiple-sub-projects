import java.util.Random;

class Farm {
    private final int width;
    private final int height;

    private final Cell[][] grid;

    private final Random random = new Random();

    private volatile boolean running = true;

    public Farm(int width, int height) {

        this.width = width;
        this.height = height;

        grid = new Cell[height][width];

        initializeFarm();
    }

    private void initializeFarm() {

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                if (isEdge(x, y)) {
                    grid[y][x] = new Cell(true, false);
                }
                else {
                    grid[y][x] = new Cell(false, false);
                }
            }
        }

        placeGates();
    }

    private boolean isEdge(int x, int y) {

        return x == 0 ||
               y == 0 ||
               x == width - 1 ||
               y == height - 1;
    }

    private void placeGates() {

        int x;

        x = random.nextInt(width - 2) + 1;
        grid[0][x] = new Cell(false, true);

        x = random.nextInt(width - 2) + 1;
        grid[height - 1][x] = new Cell(false, true);

        int y;

        y = random.nextInt(height - 2) + 1;
        grid[y][0] = new Cell(false, true);

        y = random.nextInt(height - 2) + 1;
        grid[y][width - 1] = new Cell(false, true);
    }

    public Cell get(int x, int y) {
        return grid[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized boolean sheepEscaped(
            String sheepName,
            int x,
            int y
    ) {

        if (!running) {
            return false;
        }

        running = false;

        System.out.println(
                sheepName +
                " escaped through gate at (" +
                (x + 1) +
                ", " +
                (y + 1) +
                ")"
        );

        return true;
    }

    public void stop() {
        running = false;
    }

    public boolean isMidZone(int x, int y) {

        int startX = ((width - 2) / 3) + 1;
        int endX = startX + ((width - 2) / 3) - 1;

        int startY = ((height - 2) / 3) + 1;
        int endY = startY + ((height - 2) / 3) - 1;

        return x >= startX &&
               x <= endX &&
               y >= startY &&
               y <= endY;
    }

    public void print() {

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                System.out.print(grid[y][x]);
            }

            System.out.println();
        }
    }
}