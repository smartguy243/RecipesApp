package com.smartguy.recipesapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Home : Route

    @Serializable
    data class Detail(val recipeId: Int) : Route
}