package com.smartguy.recipesapp.data.mappers

import com.smartguy.recipesapp.data.entity.RecipeEntity
import com.smartguy.recipesapp.data.model.Recipe

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