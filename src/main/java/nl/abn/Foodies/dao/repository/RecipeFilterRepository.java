package nl.abn.Foodies.dao.repository;

import nl.abn.Foodies.dao.model.Ingredient;
import nl.abn.Foodies.dao.model.Recipe;
import nl.abn.Foodies.dao.model.RecipeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class RecipeFilterRepository {

    @Autowired
    @PersistenceContext
    EntityManager em;


    public List<Recipe> findRecipesBy(RecipeFilter recipeFilter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Recipe> criteria = builder.createQuery(Recipe.class);
        Root<Recipe> rootRecipe = criteria.from(Recipe.class);
        List<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(recipeFilter.getMentioning())) {
            for (String mention : recipeFilter.getWithIngredients()) {
                predicates.add(builder.like(rootRecipe.get("details"), "%" + mention + "%"));
            }
        }

        if (!CollectionUtils.isEmpty(recipeFilter.getOfTypes())) {
            Expression<Collection<String>> languages = rootRecipe.get("types");

            for (String dishType : recipeFilter.getOfTypes()) {
                predicates.add(builder.isMember(dishType, languages));
            }
        }

        if (recipeFilter.getNoOfServings() != null) {
            Predicate noOfServingsPredicate = builder.equal(rootRecipe.get("noOfServings"), recipeFilter.getNoOfServings());
            predicates.add(noOfServingsPredicate);
        }

        if (!CollectionUtils.isEmpty(recipeFilter.getWithIngredients())) {
            Join<Recipe, Ingredient> recipeIngredientJoin = rootRecipe.join("ingredients", JoinType.INNER);

            for (String ingredient : recipeFilter.getWithIngredients()) {
                predicates.add(builder.equal(recipeIngredientJoin.get("name"), ingredient));
            }
        }


        criteria.where(builder.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Recipe> query = em.createQuery(criteria);
        return query.getResultList();
    }


}

