package com.smartguy.recipesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.model.RecipeCategory
import com.smartguy.recipesapp.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    // Состояние главного экрана
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Выбранная категория
    private val _selectedCategory = MutableStateFlow<RecipeCategory?>(null)
    val selectedCategory: StateFlow<RecipeCategory?> = _selectedCategory.asStateFlow()

    init {
        // Загружаем популярные рецепты при запуске
        loadPopularRecipes()
    }

    // Загрузка популярных рецептов
    fun loadPopularRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getRandomRecipes(number = 20).collect { result ->
                result.fold(
                    onSuccess = { recipes ->
                        _uiState.update {
                            it.copy(
                                recipes = recipes,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Ошибка загрузки рецептов"
                            )
                        }
                    }
                )
            }
        }
    }

    // Поиск рецептов
    fun searchRecipes(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            loadPopularRecipes()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.searchRecipes(query, number = 20).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.update {
                            it.copy(
                                recipes = response.results,
                                isLoading = false,
                                error = if (response.results.isEmpty()) {
                                    "По запросу \"$query\" ничего не найдено"
                                } else null,
                                totalResults = response.totalResults
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Ошибка поиска"
                            )
                        }
                    }
                )
            }
        }
    }

    // Загрузка следующей страницы (пагинация)
    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || _uiState.value.hasReachedEnd) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val currentPage = _uiState.value.currentPage
            val offset = currentPage * 20

            if (_searchQuery.value.isNotEmpty()) {
                repository.searchRecipes(
                    _searchQuery.value,
                    number = 20,
                    offset = offset
                ).collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            _uiState.update {
                                it.copy(
                                    recipes = it.recipes + response.results,
                                    isLoadingMore = false,
                                    currentPage = currentPage + 1,
                                    hasReachedEnd = response.results.isEmpty()
                                )
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(isLoadingMore = false)
                            }
                        }
                    )
                }
            }
        }
    }

    // Выбор категории
    fun selectCategory(category: RecipeCategory?) {
        _selectedCategory.value = category

        if (category != null) {
            searchRecipes(category.apiName)
        } else {
            loadPopularRecipes()
        }
    }

    // Обновление списка
    fun refresh() {
        if (_searchQuery.value.isNotEmpty()) {
            searchRecipes(_searchQuery.value)
        } else {
            loadPopularRecipes()
        }
    }
}

// Состояние главного экрана
data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalResults: Int = 0,
    val hasReachedEnd: Boolean = false
)