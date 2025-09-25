package com.smartguy.recipesapp.data.dao

import androidx.room.*
import com.smartguy.recipesapp.data.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // Получить все рецепты
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    // Получить рецепт по ID
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): RecipeEntity?

    // Поиск рецептов по названию
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    // Вставка или обновление рецептов
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    // Вставка одного рецепта
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    // Обновление статуса избранного
    @Query("UPDATE recipes SET favorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean)

    // Получить избранные рецепты
    @Query("SELECT * FROM recipes WHERE favorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    // Удаление всех рецептов
    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    // Получить рецепты с пагинацией
    @Query("SELECT * FROM recipes LIMIT :limit OFFSET :offset")
    suspend fun getRecipesWithPagination(limit: Int, offset: Int): List<RecipeEntity>

    // Получить количество рецептов
    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipesCount(): Int

    // Поиск рецептов по категории
    @Query("SELECT * FROM recipes WHERE glutenFree = :glutenFree")
    fun getRecipesByDietaryPreference(glutenFree: Boolean): Flow<List<RecipeEntity>>
}