import java.util.*;

public class Recipes {
    private static final List<List<Ingredient>> RECIPES = List.of(
            List.of(Ingredient.BUNS, Ingredient.MEAT, Ingredient.CHEESE),
            List.of(Ingredient.BUNS, Ingredient.MEAT, Ingredient.LETTUCE),
            List.of(Ingredient.BUNS, Ingredient.MEAT, Ingredient.SAUCE, Ingredient.CHEESE),
            List.of(Ingredient.BUNS, Ingredient.CHEESE, Ingredient.LETTUCE, Ingredient.SAUCE)
    );

    public static List<Ingredient> getRandomRecipe() {
        Random r = new Random();
        return new ArrayList<>(RECIPES.get(r.nextInt(RECIPES.size())));
    }
}
