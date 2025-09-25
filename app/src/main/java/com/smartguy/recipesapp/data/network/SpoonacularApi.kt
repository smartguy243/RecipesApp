package com.smartguy.recipesapp.data.network

import com.smartguy.recipesapp.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("/recipes/random")
    suspend fun getRandomRecipes(
        @Query("includeNutrition") includeNutrition: Boolean=true,
        @Query("include-tags") includeTags: String = "vegetarian, dessert",
        @Query("exclude-tags") excludeTags: String = "dairy",
        @Query("number") number: Int = 1
    ): SearchResponse

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("addRecipeInformation") addInfo: Boolean = true
    ): SearchResponse
}