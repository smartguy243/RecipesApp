package com.smartguy.recipesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartguy.recipesapp.data.model.IngredientItem
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {

    // Состояние экрана деталей
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Загрузка деталей рецепта
    fun loadRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)

            repository.getRecipeDetails(recipeId).fold(
                onSuccess = { recipe ->
                    _uiState.value = DetailUiState(
                        recipe = recipe,
                        isLoading = false
                    )
                    loadRecipeIngredients(recipeId)
                },
                onFailure = { exception ->
                    _uiState.value = DetailUiState(
                        error = exception.message ?: "Ошибка загрузки рецепта",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun loadRecipeIngredients(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIngredients = true, ingredientsError = null) }

            repository.getRecipeIngredients(recipeId).collect { result ->
                result.fold(
                    onSuccess = { ingredients ->
                        _uiState.update {
                            it.copy(
                                ingredients = ingredients,
                                isLoadingIngredients = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoadingIngredients = false,
                                ingredientsError = exception.message
                                    ?: "Erreur lors du chargement des ingrédients"
                            )
                        }
                    }
                )
            }
        }
    }

    // Переключение избранного
    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId)
            _uiState.value = _uiState.value.copy(
                isFavorite = !_uiState.value.isFavorite
            )
        }
    }

    // Обновление деталей
    fun refresh(recipeId: Int) {
        loadRecipeDetails(recipeId)
    }
}

// Состояние экрана деталей
data class DetailUiState(
    val recipe: Recipe? = null,
    val ingredients: List<IngredientItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingIngredients: Boolean = false,
    val error: String? = null,
    val ingredientsError: String? = null,
    val isFavorite: Boolean = false
)