package com.smartguy.recipesapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartguy.recipesapp.data.dao.RecipeDao
import com.smartguy.recipesapp.data.entity.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun recipeDao(): RecipeDao
}