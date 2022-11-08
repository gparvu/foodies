package nl.abn.Foodies.dao.model;

import lombok.Data;

import java.util.Set;

@Data
public class RecipeFilter {

    Set<String> ofTypes;
    Set<String> notOfTypes;
    Integer noOfServings;
    Set<String> withIngredients;
    Set<String> mentioning;

}
