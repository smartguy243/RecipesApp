package com.smartguy.recipesapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartguy.recipesapp.data.dao.IngredientDao
import com.smartguy.recipesapp.data.dao.RecipeDao
import com.smartguy.recipesapp.data.entity.IngredientEntity
import com.smartguy.recipesapp.data.entity.RecipeEntity

@Database(entities = [RecipeEntity::class, IngredientEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
}