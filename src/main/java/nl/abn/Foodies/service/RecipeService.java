package nl.abn.Foodies.service;

import nl.abn.Foodies.api.model.RecipePayload;
import nl.abn.Foodies.dao.model.Recipe;
import nl.abn.Foodies.dao.model.RecipeFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RecipeService {
    RecipePayload createRecipe(RecipePayload RecipePayload);

    RecipePayload updateRecipe(RecipePayload RecipePayload);

    void deleteRecipe(Integer RecipeId);

    RecipePayload getRecipe(Integer RecipeId);
    List<RecipePayload> findRecipesBy(RecipeFilter filter);
}
