import java.util.concurrent.TimeUnit;

public enum Ingredient {
    BUNS(200),
    MEAT(400),
    CHEESE(150),
    LETTUCE(100),
    SAUCE(80);

    private final int preparationTimeMs;

    Ingredient(int preparationTimeMs) {
        this.preparationTimeMs = preparationTimeMs;
    }

    public int getPreparationTime() {
        return preparationTimeMs;
    }

    @Override
    public String toString() {
        return name();
    }
}
