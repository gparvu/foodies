package nl.abn.Foodies.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

/**
 * Recipe
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipePayload {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("noOfServings")
    private Long noOfServings;

    @JsonProperty("type")
    private List<String> types;

    @JsonProperty("ingredients")
    @Valid
    private List<String> ingredients = null;

    @JsonProperty("details")
    private String details;
}

