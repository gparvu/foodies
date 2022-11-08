package nl.abn.Foodies.service;

import nl.abn.Foodies.api.model.RecipePayload;
import nl.abn.Foodies.dao.model.Ingredient;
import nl.abn.Foodies.dao.model.Recipe;
import nl.abn.Foodies.dao.model.RecipeFilter;
import nl.abn.Foodies.dao.repository.IngredientRepository;
import nl.abn.Foodies.dao.repository.RecipeFilterRepository;
import nl.abn.Foodies.dao.repository.RecipeRepository;
import nl.abn.Foodies.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeFilterRepository recipeFilterRepository;
    @Autowired
    private IngredientRepository ingredientRepository;


    @Override
    public RecipePayload createRecipe(RecipePayload recipePayload) {
        Recipe newRecipe = new Recipe();

        newRecipe.setId(recipePayload.getId());
        newRecipe.setName(recipePayload.getName());
        newRecipe.setNoOfServings(recipePayload.getNoOfServings());
        newRecipe.setTypes(new HashSet<>(recipePayload.getTypes()));
        newRecipe.setIngredients(processIngredients(recipePayload.getIngredients()));
        recipeRepository.save(newRecipe);

        return createRecipePayload(newRecipe);

    }

    private Set<Ingredient> processIngredients(List<String> ingredientsPayload) {

        List<Ingredient> ingredients = ingredientsPayload
                .stream()
                .map(ingredient -> new Ingredient(ingredient))
                .collect(Collectors.toList());
        return new HashSet<>(ingredientRepository.saveAll(ingredients));
    }


    @Override
    public RecipePayload updateRecipe(RecipePayload recipePayload) throws ResourceNotFoundException {
        Optional<Recipe> existingRecipe = recipeRepository.findById(recipePayload.getId());
        if (existingRecipe.isPresent()) {
            Recipe newRecipe = existingRecipe.get();
            newRecipe.setName(recipePayload.getName());
            newRecipe.setNoOfServings(recipePayload.getNoOfServings());
            newRecipe.setDetails(recipePayload.getDetails());
            newRecipe.setTypes(new HashSet<>(recipePayload.getTypes()));
            newRecipe.setIngredients(processIngredients(recipePayload.getIngredients()));
            return createRecipePayload(recipeRepository.save(newRecipe));
        }

        throw new ResourceNotFoundException("Recipe", "id", recipePayload.getId());
    }


    @Override
    public RecipePayload getRecipe(Integer recipeId) throws ResourceNotFoundException {
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            return createRecipePayload(recipe.get());
        }

        throw new ResourceNotFoundException("Recipe", "id", recipeId);
    }

    @Override
    public List<RecipePayload> findRecipesBy(RecipeFilter filter) {
        List<Recipe> recipes = recipeFilterRepository.findRecipesBy(filter);

        return recipes.stream()
                .map(RecipeServiceImpl::createRecipePayload)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteRecipe(Integer recipeId) throws ResourceNotFoundException {
        Optional<Recipe> existingRecipe = recipeRepository.findById(recipeId);
        if (!existingRecipe.isPresent()) {
            throw new ResourceNotFoundException("Recipe", "id", recipeId);
        }

        recipeRepository.deleteById(recipeId);
    }

    private static RecipePayload createRecipePayload(Recipe recipe) {
        RecipePayload recipePayload = RecipePayload.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .types(new ArrayList<>(recipe.getTypes()))
                .noOfServings(recipe.getNoOfServings())
                .ingredients(createIngredientsPayload(recipe.getIngredients()))
                .build();

        return recipePayload;
    }

    private static List<String> createIngredientsPayload(Set<Ingredient> ingredients) {
        List<String> ingredientsPayload = ingredients
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());

        return ingredientsPayload;
    }

}
