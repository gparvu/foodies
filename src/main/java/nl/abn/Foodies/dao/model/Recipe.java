package nl.abn.Foodies.dao.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

/**
 * Recipe
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    private Integer id;

    private String name;
    private Long noOfServings;

    @ElementCollection(fetch = EAGER)
    private Set<String> types;


    @ManyToMany(cascade = {CascadeType.ALL}, fetch = EAGER)
    @JoinTable(
            name = "Recipe_ingredient",
            joinColumns = {@JoinColumn(name = "recipe_id")},
            inverseJoinColumns = {@JoinColumn(name = "ingredient_id")}
    )
    private Set<Ingredient> ingredients;

    private String details;

}

