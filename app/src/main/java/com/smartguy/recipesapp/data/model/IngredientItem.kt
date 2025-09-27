package com.smartguy.recipesapp.data.model

data class IngredientResponse(
    val ingredients: List<IngredientItem>
)
data class IngredientItem(
    val amount: Amount,
    val image: String,
    val name: String
)

data class Amount(
    val metric: Metric,
    val us: Us
)

data class Us(
    val unit: String,
    val value: Double
)

data class Metric(
    val unit: String,
    val value: Double
)