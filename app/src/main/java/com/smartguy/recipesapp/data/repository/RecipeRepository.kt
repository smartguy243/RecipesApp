package com.smartguy.recipesapp.data.repository

import com.smartguy.recipesapp.data.dao.RecipeDao
import com.smartguy.recipesapp.data.entity.RecipeEntity
import com.smartguy.recipesapp.data.mappers.toEntity
import com.smartguy.recipesapp.data.mappers.toRecipe
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.model.SearchResponse
import com.smartguy.recipesapp.data.network.SpoonacularApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val api: SpoonacularApi,
    private val recipeDao: RecipeDao
) {
    // Получение случайных рецептов (для главного экрана)
    suspend fun getRandomRecipes(number: Int = 20): Flow<Result<List<Recipe>>> = flow {
        try {
            // Сначала пытаемся загрузить из сети
            val response = api.getRandomRecipes(
                includeNutrition = false,
                includeTags = "",
                excludeTags = "",
                number = number
            )

            val recipes = response.recipes.ifEmpty { response.results }

            // Сохраняем в базу данных для оффлайн доступа
            val entities = recipes.map { it.toEntity() }
            recipeDao.insertRecipes(entities)

            emit(Result.success(recipes))
        } catch (e: Exception) {
            // Если нет интернета, загружаем из кэша
            try {
                val cachedRecipes = recipeDao.getAllRecipes()
                cachedRecipes.collect { entities ->
                    val recipes = entities.map { it.toRecipe() }
                    emit(Result.success(recipes))
                }
            } catch (cacheError: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    // Поиск рецептов
    fun searchRecipes(
        query: String,
        number: Int = 20,
        offset: Int = 0
    ): Flow<Result<SearchResponse>> = flow {
        try {
            val response = api.searchRecipes(
                query = query,
                number = number,
                offset = offset,
                addInfo = true
            )

            // Сохраняем результаты поиска в базу данных
            val entities = response.results.map { it.toEntity() }
            recipeDao.insertRecipes(entities)

            emit(Result.success(response))
        } catch (e: Exception) {
            // Поиск в кэше при отсутствии интернета
            try {
                recipeDao.searchRecipes(query).collect { entities ->
                    val recipes = entities.map { it.toRecipe() }
                    val response = SearchResponse(
                        results = recipes,
                        offset = offset,
                        number = recipes.size,
                        totalResults = recipes.size
                    )
                    emit(Result.success(response))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    // Получение деталей рецепта
    suspend fun getRecipeDetails(recipeId: Int): Result<Recipe> {
        return try {
            // Попытка загрузить из сети
            val response = api.searchRecipes(
                query = "",
                number = 1,
                offset = 0,
                addInfo = true
            )

            // В реальном приложении здесь был бы отдельный endpoint для деталей
            // Для демонстрации используем существующий
            val recipe = response.results.find { it.id == recipeId }

            if (recipe != null) {
                // Сохраняем в кэш
                val entity = RecipeEntity(
                    id = recipe.id,
                    title = recipe.title,
                    imageUrl = recipe.image ?: "",
                    servings = recipe.servings,
                    readyInMinutes = recipe.readyInMinutes,
                    likes = recipe.aggregateLikes,
                    glutenFree = recipe.glutenFree
                )
                recipeDao.insertRecipe(entity)
                Result.success(recipe)
            } else {
                // Если не найдено в сети, ищем в кэше
                val cached = recipeDao.getRecipeById(recipeId)
                if (cached != null) {
                    Result.success(
                        Recipe(
                            id = cached.id,
                            title = cached.title,
                            image = cached.imageUrl,
                            servings = cached.servings,
                            readyInMinutes = cached.readyInMinutes,
                            aggregateLikes = cached.likes ?: 0,
                            glutenFree = cached.glutenFree ?: false,
                            summary = cached.summary ?: ""
                        )
                    )
                } else {
                    Result.failure(Exception("Recipe not found"))
                }
            }
        } catch (e: Exception) {
            // При ошибке сети ищем в кэше
            val cached = recipeDao.getRecipeById(recipeId)
            if (cached != null) {
                Result.success(
                    Recipe(
                        id = cached.id,
                        title = cached.title,
                        image = cached.imageUrl,
                        servings = cached.servings,
                        readyInMinutes = cached.readyInMinutes,
                        aggregateLikes = cached.likes ?: 0,
                        glutenFree = cached.glutenFree ?: false,
                        summary = cached.summary ?: ""
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }

    // Управление избранными рецептами
    suspend fun toggleFavorite(recipeId: Int) {
        val recipe = recipeDao.getRecipeById(recipeId)
        recipe?.let {
            recipeDao.updateFavoriteStatus(recipeId, !it.favorite)
        }
    }

    // Получение избранных рецептов
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.getFavoriteRecipes()
    }

    // Очистка кэша
    suspend fun clearCache() {
        recipeDao.deleteAllRecipes()
    }
}