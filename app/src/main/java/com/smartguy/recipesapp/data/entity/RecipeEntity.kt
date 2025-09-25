package com.smartguy.recipesapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity (
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val favorite: Boolean = false,
    val servings: Int? = null,
    val readyInMinutes: Int? = null,
    val likes: Int? =  null,
    val glutenFree: Boolean? = null,
    val summary: String? = null
)