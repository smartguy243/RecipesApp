package com.smartguy.recipesapp.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Получаем ID рецепта из навигационных аргументов

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
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)