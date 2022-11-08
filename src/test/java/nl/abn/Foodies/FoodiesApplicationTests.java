package nl.abn.Foodies;


import nl.abn.Foodies.api.model.RecipePayload;
import nl.abn.Foodies.api.resources.RecipeApi;
import nl.abn.Foodies.dao.model.Ingredient;
import nl.abn.Foodies.dao.model.Recipe;
import nl.abn.Foodies.dao.repository.RecipeRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class FoodiesApplicationTests {

    public static final List<String> AVO_TOAST_DISH_TYPES = Lists.list("Vegan", "Paleo");
    public static final String AVO_TOAST = "Avo Toast";
    public static final String SALMON_AVOCADO = "Salmon & Avocado";

    @Autowired
    private RecipeRepository repository;

    @Autowired
    private RecipeApi recipeApi;

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }

    @Test
    void saveRecipe() {
        ResponseEntity<RecipePayload> response = recipeApi.addRecipe(createAvoToastPayload());

        assertTrue(response.hasBody());
        assertNotNull(response.getBody().getId());
        assertEquals(AVO_TOAST, response.getBody().getName());
        assertTrue(response.getBody().getTypes().containsAll(Lists.list("Vegan", "Paleo")));
        assertTrue(response.getBody().getIngredients().containsAll(Lists.list("Avocado", "Bread")));
    }

    private static RecipePayload createAvoToastPayload() {
        RecipePayload avoToast = new RecipePayload();
        avoToast.setId(1);
        avoToast.setNoOfServings(2L);
        avoToast.setName(AVO_TOAST);
        avoToast.setTypes(AVO_TOAST_DISH_TYPES);
        avoToast.setIngredients(Lists.list("Avocado", "Bread"));

        return avoToast;
    }

    @Test
    void getRecipe() {
        initDbWithAvoToastRecipe();

        ResponseEntity<RecipePayload> response = recipeApi.getRecipeById(1);

        assertTrue(response.hasBody());
        assertEquals(1, response.getBody().getId());
        assertEquals(2, response.getBody().getNoOfServings());
        assertEquals(AVO_TOAST, response.getBody().getName());
        assertTrue(response.getBody().getTypes().containsAll(Lists.list("Vegan", "Paleo")));

    }

    @Test
    void updateRecipe() {
        //arrange
        initDbWithAvoToastRecipe();

        RecipePayload updatedRecipePayload = createAvoToastPayload();
        updatedRecipePayload.setName("Avo Toastalicious");
        updatedRecipePayload.setIngredients(Lists.list("Banana"));

        //act
        ResponseEntity<RecipePayload> response = recipeApi.updateRecipe(updatedRecipePayload);

        //assert
        assertTrue(response.hasBody());
        assertEquals(1, response.getBody().getId());
        assertEquals(2, response.getBody().getNoOfServings());
        assertEquals("Avo Toastalicious", response.getBody().getName());
        assertTrue(response.getBody().getTypes().containsAll(Lists.list("Vegan", "Paleo")));
        assertEquals(1, response.getBody().getIngredients().size());
        assertTrue(response.getBody().getIngredients().contains("Banana"));

    }

    @Test
    void deleteRecipe() {
        initDbWithAvoToastRecipe();

        recipeApi.deleteRecipe(1);

        assertFalse(repository.existsById(1));
    }

    @Test
    void getVeganRecipes() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy("Vegan", null, 2, null, null);

        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getId());
        assertEquals(2, response.getBody().get(0).getNoOfServings());
        assertEquals(AVO_TOAST, response.getBody().get(0).getName());
        assertTrue(response.getBody().get(0).getTypes().contains("Vegan"));
    }

    @Test
    void getNonVeganRecipes() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy(null, "Vegan", null, null, null);

        assertEquals(1, response.getBody().size());
        assertEquals(SALMON_AVOCADO, response.getBody().get(0).getName());
        assertFalse(response.getBody().get(0).getTypes().contains("Vegan"));
    }

    @Test
    void getPaleoRecipes() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy("Paleo", null, null, null, null);

        assertEquals(2, response.getBody().size());
    }

    @Test
    void getRecipesWithAvocado() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy(null, null, null, "Avocado", null);

        assertEquals(2, response.getBody().size());
    }

    @Test
    void getRecipesWithAvocadoMentioningBake() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy(null, null, null, "Avocado", "bake");

        assertEquals(1, response.getBody().size());
        assertEquals("Avo Toast", response.getBody().get(0).getName());
    }

    @Test
    void getRecipesWithAvocadoAndSalmon() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy(null, null, null, "Avocado,Salmon", null);

        assertEquals(1, response.getBody().size());
        assertEquals("Salmon & Avocado", response.getBody().get(0).getName());
    }

    @Test
    void getRecipesMentioningBake() {
        initDbWithRecipes();

        ResponseEntity<List<RecipePayload>> response = recipeApi.findRecipesBy(null, null, null, null, "bake");

        assertEquals(1, response.getBody().size());
        assertEquals("Avo Toast", response.getBody().get(0).getName());
    }

    private void initDbWithRecipes() {
        List<Recipe> initialRecipes = new ArrayList<>();
        initialRecipes.add(createAvoToastRecipe());
        initialRecipes.add(createSalmonWithAvocado());

        repository.saveAll(initialRecipes);
    }

    private Recipe createSalmonWithAvocado() {
        Recipe salmonWithAvocado = new Recipe();
        salmonWithAvocado.setId(2);
        salmonWithAvocado.setNoOfServings(1L);
        salmonWithAvocado.setName(SALMON_AVOCADO);
        salmonWithAvocado.setTypes(new HashSet<>(Lists.list("Carnivorous", "Paleo")));

        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(new Ingredient("Salmon"));
        ingredients.add(new Ingredient("Avocado"));
        salmonWithAvocado.setIngredients(ingredients);

        return salmonWithAvocado;
    }


    public void initDbWithAvoToastRecipe() {
        Recipe avoToast = createAvoToastRecipe();
        repository.save(avoToast);
    }

    private static Recipe createAvoToastRecipe() {
        Recipe avoToast = new Recipe();
        avoToast.setId(1);
        avoToast.setNoOfServings(2L);
        avoToast.setName("Avo Toast");
        avoToast.setTypes(new HashSet<>(Lists.list("Vegan", "Paleo")));
        avoToast.setDetails("we bake the bread and spread the avocado on it");

        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(new Ingredient("Bread"));
        ingredients.add(new Ingredient("Avocado"));
        avoToast.setIngredients(ingredients);

        return avoToast;
    }

}
