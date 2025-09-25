package com.smartguy.recipesapp.data.model

import com.google.gson.annotations.SerializedName

// Модель для ответа от API
data class SearchResponse(
    @SerializedName("results")
    val results: List<Recipe> = emptyList(),
    @SerializedName("recipes")
    val recipes: List<Recipe> = emptyList(),
    @SerializedName("offset")
    val offset: Int = 0,
    @SerializedName("number")
    val number: Int = 0,
    @SerializedName("totalResults")
    val totalResults: Int = 0
)

// Основная модель рецепта
data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("imageType")
    val imageType: String? = null,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int? = null,
    @SerializedName("servings")
    val servings: Int? = null,
    @SerializedName("sourceUrl")
    val sourceUrl: String? = null,
    @SerializedName("vegetarian")
    val vegetarian: Boolean = false,
    @SerializedName("vegan")
    val vegan: Boolean = false,
    @SerializedName("glutenFree")
    val glutenFree: Boolean = false,
    @SerializedName("dairyFree")
    val dairyFree: Boolean = false,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean = false,
    @SerializedName("cheap")
    val cheap: Boolean = false,
    @SerializedName("veryPopular")
    val veryPopular: Boolean = false,
    @SerializedName("sustainable")
    val sustainable: Boolean = false,
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int = 0,
    @SerializedName("healthScore")
    val healthScore: Double = 0.0,
    @SerializedName("creditsText")
    val creditsText: String? = null,
    @SerializedName("sourceName")
    val sourceName: String? = null,
    @SerializedName("pricePerServing")
    val pricePerServing: Double = 0.0,
    @SerializedName("cuisines")
    val cuisines: List<String> = emptyList(),
    @SerializedName("dishTypes")
    val dishTypes: List<String> = emptyList(),
    @SerializedName("diets")
    val diets: List<String> = emptyList(),
    @SerializedName("occasions")
    val occasions: List<String> = emptyList(),
    @SerializedName("analyzedInstructions")
    val analyzedInstructions: List<Instruction> = emptyList(),
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<Ingredient> = emptyList(),
    @SerializedName("nutrition")
    val nutrition: Nutrition? = null
)

// Модель для инструкций
data class Instruction(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("steps")
    val steps: List<Step> = emptyList()
)

data class Step(
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String,
    @SerializedName("ingredients")
    val ingredients: List<IngredientStep> = emptyList(),
    @SerializedName("equipment")
    val equipment: List<Equipment> = emptyList()
)

data class IngredientStep(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("localizedName")
    val localizedName: String? = null,
    @SerializedName("image")
    val image: String? = null
)

data class Equipment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("localizedName")
    val localizedName: String? = null,
    @SerializedName("image")
    val image: String? = null
)

// Модель для ингредиентов
data class Ingredient(
    @SerializedName("id")
    val id: Int,
    @SerializedName("aisle")
    val aisle: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("consistency")
    val consistency: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameClean")
    val nameClean: String? = null,
    @SerializedName("original")
    val original: String,
    @SerializedName("originalName")
    val originalName: String? = null,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("meta")
    val meta: List<String> = emptyList(),
    @SerializedName("measures")
    val measures: Measures? = null
)

data class Measures(
    @SerializedName("us")
    val us: Measure,
    @SerializedName("metric")
    val metric: Measure
)

data class Measure(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unitShort")
    val unitShort: String,
    @SerializedName("unitLong")
    val unitLong: String
)

// Модель для питательной ценности
data class Nutrition(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient> = emptyList(),
    @SerializedName("properties")
    val properties: List<Property> = emptyList(),
    @SerializedName("flavonoids")
    val flavonoids: List<Flavonoid> = emptyList(),
    @SerializedName("ingredients")
    val ingredients: List<IngredientNutrition> = emptyList(),
    @SerializedName("caloricBreakdown")
    val caloricBreakdown: CaloricBreakdown? = null
)

data class Nutrient(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("percentOfDailyNeeds")
    val percentOfDailyNeeds: Double? = null
)

data class Property(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String
)

data class Flavonoid(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String
)

data class IngredientNutrition(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("nutrients")
    val nutrients: List<Nutrient> = emptyList()
)

data class CaloricBreakdown(
    @SerializedName("percentProtein")
    val percentProtein: Double,
    @SerializedName("percentFat")
    val percentFat: Double,
    @SerializedName("percentCarbs")
    val percentCarbs: Double
)

// Категории для фильтрации
enum class RecipeCategory(val displayName: String, val apiName: String) {
    MAIN_COURSE("Main Course", "main course"),
    SIDE_DISH("Side Dish", "side dish"),
    DESSERT("Dessert", "dessert"),
    APPETIZER("Appetizer", "appetizer"),
    SALAD("Salad", "salad"),
    BREAD("Bread", "bread"),
    BREAKFAST("Breakfast", "breakfast"),
    SOUP("Soup", "soup"),
    BEVERAGE("Beverage", "beverage"),
    SAUCE("Sauce", "sauce"),
    MARINADE("Marinade", "marinade"),
    FINGERFOOD("Fingerfood", "fingerfood"),
    SNACK("Snack", "snack"),
    DRINK("Drink", "drink")
}