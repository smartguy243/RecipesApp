package com.smartguy.recipesapp.data.dao

import androidx.room.*
import com.smartguy.recipesapp.data.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    // Получить ингредиенты для конкретной рецепта
    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId")
    fun getIngredientsByRecipeId(recipeId: Int): Flow<List<IngredientEntity>>

    // Вставить ингредиенты в базу данных
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    // Удалить ингредиенты для конкретной рецепта
    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Int)

    // Проверить, есть ли кэшированные ингредиенты для рецепта
    @Query("SELECT COUNT(*) FROM ingredients WHERE recipeId = :recipeId")
    suspend fun hasCachedIngredients(recipeId: Int): Int
}