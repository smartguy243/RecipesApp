package com.smartguy.recipesapp.di

import android.content.Context
import androidx.room.Room
import com.smartguy.recipesapp.data.dao.RecipeDao
import com.smartguy.recipesapp.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "recipe_database").build()

    @Provides @Singleton
    fun provideRecipeDao(db: AppDatabase):
            RecipeDao = db.recipeDao()
}