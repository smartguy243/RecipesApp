package com.smartguy.recipesapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.smartguy.recipesapp.presentation.screen.DetailScreen
import com.smartguy.recipesapp.presentation.screen.HomeScreen

@Composable
fun RecipeNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        // Главный экран
        composable<Route.Home> {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Route.Detail(recipeId))
                }
            )
        }

        // Экран деталей рецепта
        composable<Route.Detail> {
            val detailRoute = it.toRoute<Route.Detail>()
            DetailScreen(
                recipeId = detailRoute.recipeId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

