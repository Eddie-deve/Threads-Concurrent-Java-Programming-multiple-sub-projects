import java.util.concurrent.locks.ReentrantLock;

class Cell {
    final ReentrantLock lock = new ReentrantLock();

    Object occupant;

    final boolean isWall;
    final boolean isGate;

    public Cell(boolean isWall, boolean isGate) {
        this.isWall = isWall;
        this.isGate = isGate;
        this.occupant = null;
    }

    public boolean isFree() {
        return !isWall && occupant == null;
    }

    @Override
    public String toString() {
        if (isWall) {
            return "#";
        }

        if (occupant != null) {
            return occupant.toString();
        }

        return " ";
    }
}