package com.smartguy.recipesapp.data.mappers

import com.smartguy.recipesapp.data.entity.IngredientEntity
import com.smartguy.recipesapp.data.entity.RecipeEntity
import com.smartguy.recipesapp.data.model.Amount
import com.smartguy.recipesapp.data.model.IngredientItem
import com.smartguy.recipesapp.data.model.Metric
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.model.Us

fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id = this.id,
    title = this.title,
    imageUrl = this.image ?: "",
    servings = this.servings,
    readyInMinutes = this.readyInMinutes,
    likes = this.aggregateLikes,
    glutenFree = this.glutenFree,
    summary = this.summary
)


fun RecipeEntity.toRecipe(): Recipe = Recipe(
    id = this.id,
    title = this.title,
    image = this.imageUrl,
    servings = this.servings,
    readyInMinutes = this.readyInMinutes,
    aggregateLikes = this.likes ?: 0,
    glutenFree = this.glutenFree == true,
    summary = this.summary ?: ""
)

fun IngredientItem.toEntity(recipeId: Int): IngredientEntity {
    return IngredientEntity(
        recipeId = recipeId,
        name = this.name,
        amount = this.amount.metric.value,
        unit = this.amount.metric.unit,
        imageUrl = this.image
    )
}

fun IngredientEntity.toIngredientItem(): IngredientItem {
    return IngredientItem(
        amount = Amount(
            metric = Metric(
                value = this.amount,
                unit = this.unit
            ),
            us = Us(
                value = this.amount * 1.05,
                unit = when (this.unit) {
                    "g" -> "oz"
                    "ml" -> "cup"
                    else -> this.unit
                }
            )
        ),
        image = this.imageUrl ?: "",
        name = this.name
    )
}