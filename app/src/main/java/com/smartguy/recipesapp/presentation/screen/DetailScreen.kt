package com.smartguy.recipesapp.presentation.screen

import android.R
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AccessTime // Changed from Timer for a common icon
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group // Changed from People for a common icon
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Restaurant // Example, you might need a better icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smartguy.recipesapp.data.model.Ingredient
import com.smartguy.recipesapp.data.model.Recipe
import com.smartguy.recipesapp.data.model.Step
import com.smartguy.recipesapp.presentation.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSummaryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetails(recipeId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // uiState.recipe?.title ?: "Recipe Details", // You can make title dynamic
                        "Recipe Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    uiState.recipe?.let { // Show favorite only if recipe is loaded
                        IconButton(
                            onClick = { viewModel.toggleFavorite(recipeId) }
                        ) {
                            Icon(
                                if (uiState.isFavorite) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (uiState.isFavorite) Color.Red
                                else MaterialTheme.colorScheme.onPrimary // Adjusted for top app bar
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error ?: "An unknown error occurred.",
                    onRetry = { viewModel.refresh(recipeId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.recipe != null -> {
                RecipeDetailsContent(
                    recipe = uiState.recipe!!,
                    showSummaryExpanded = showSummaryExpanded,
                    onSummaryExpandToggle = { showSummaryExpanded = !showSummaryExpanded },
                    modifier = Modifier.padding(paddingValues) // Pass paddingValues here
                )
            }
        }
    }
}

@Composable
fun RecipeDetailsContent(
    recipe: Recipe,
    showSummaryExpanded: Boolean,
    onSummaryExpandToggle: () -> Unit,
    modifier: Modifier = Modifier // This modifier will now include padding from Scaffold
) {
    LazyColumn(
        modifier = modifier.fillMaxSize() // Use the passed modifier
    ) {
        // --- Image Section ---
        item {
            Box(modifier = Modifier.fillMaxWidth()) { // Use Box for potential overlay positioning
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp), // More rounded corners
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // White background
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // Adjusted height
                        placeholder = painterResource(R.drawable.ic_menu_gallery),
                        error = painterResource(R.drawable.ic_menu_gallery)
                    )
                }
            }
        }

        // --- Title and Basic Info Overlay ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-60).dp),
                shape = RoundedCornerShape(24.dp), // More rounded corners
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // White background
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp) // Increased padding
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally // Center title
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface // Black text
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally), // Space out and center
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RecipeInfoItem(
                            icon = Icons.Outlined.Group, // Changed icon
                            label = "${recipe.servings ?: 0} Persons"
                        )

                        RecipeInfoItem(
                            icon = Icons.Outlined.AccessTime, // Changed icon
                            label = "${recipe.readyInMinutes ?: 0} Minutes"
                        )
                    }
                }
            }
        }

        // --- Recipe Summary Section ---
        item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                        .offset(y = (-45).dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (showSummaryExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)
                                else Color.Unspecified),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 80.dp),
                                text = "Recipe Summary",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant // Darker text on lighter bg
                            )
                            IconButton(
                                onClick = onSummaryExpandToggle,
                                modifier = Modifier.size(32.dp) // Slightly larger icon button
                            ) {
                                Icon(
                                    modifier = Modifier.size(50.dp),
                                    imageVector = if (showSummaryExpanded) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (showSummaryExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.primary // Keep primary color for icon
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        if (showSummaryExpanded) {

                            val htmlRegex = "</?[^>]+(>|\$)".toRegex()
                            val cleanSummary = recipe.summary?.replace(htmlRegex, "") ?: "No summary available."

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Justify,
                                text = cleanSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
        }


        // --- Ingredients Section ---
        if (recipe.extendedIngredients.isNotEmpty()) {
            item {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface // Black text
                )
            }

            items(recipe.extendedIngredients) { ingredient ->
                IngredientItem(ingredient, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }

        // --- Instructions Section ---
        if (recipe.analyzedInstructions.isNotEmpty()) {
            item {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            recipe.analyzedInstructions.forEach { instruction ->
                items(instruction.steps) { step ->
                    StepItem(step, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }

        // --- Nutrition Section (Optional, if you want to keep it) ---
        recipe.nutrition?.let { nutrition ->
            item {
                NutritionSection(nutrition, modifier = Modifier.padding(top = 16.dp))
            }
        }

        // --- Dietary Tags & Additional Info (Consider where to best place these or if they are still needed) ---
        // item { RecipeTags(recipe, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) }
        // item { RecipeAdditionalInfo(recipe, modifier = Modifier.padding(16.dp)) }


        // Bottom Spacer
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun RecipeInfoItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier // Added modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp), // Slightly more space
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = null, // Content description can be label itself if needed for accessibility
            modifier = Modifier.size(20.dp), // Adjusted size to match image
            tint = MaterialTheme.colorScheme.onSurfaceVariant // Greyish tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Greyish text
        )
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    modifier: Modifier = Modifier // Added modifier
) {
    Row(
        modifier = modifier // Use the passed modifier for padding
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Padding only vertical, horizontal is handled by parent
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp) // Slightly larger
                .clip(RoundedCornerShape(12.dp)) // More rounded
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}",
                contentDescription = ingredient.name,
                modifier = Modifier.size(36.dp), // Adjusted size
                placeholder = painterResource(id = R.drawable.picture_frame), // Generic placeholder
                error = painterResource(id = R.drawable.stat_notify_error) // Generic error
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ingredient.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, // Capitalize
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = ingredient.original, // You might want to format this better
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Removed the explicit amount and unit display here as the image seems to omit it for a cleaner look
        // If you need it, you can add it back:
        // Text(
        // text = "${ingredient.amount.toPrettyString()} ${ingredient.unit}",
        // style = MaterialTheme.typography.bodyMedium,
        // color = MaterialTheme.colorScheme.primary
        // )
    }
}

@Composable
fun StepItem(step: Step, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        // .padding(horizontal = 16.dp, vertical = 4.dp), // Padding handled by LazyColumn item
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Or surfaceVariant for less emphasis
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat look
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp) // Adjusted padding
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp) // Slightly smaller
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step.number}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = step.step,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun NutritionSection(nutrition: com.smartguy.recipesapp.data.model.Nutrition, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp) // Added vertical padding
    ) {
        Text(
            text = "Nutrition Information",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp) // Increased padding
            ) {
                // To match the image, you might only show a few key nutrients or none at all
                nutrition.nutrients.take(4).forEach { nutrient -> // Show fewer nutrients
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = nutrient.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${nutrient.amount.toInt()} ${nutrient.unit}", // Simplified amount
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }

                nutrition.caloricBreakdown?.let { breakdown ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Caloric Breakdown",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround // Use SpaceAround for better distribution
                    ) {
                        CaloricBreakdownItem("Protein", breakdown.percentProtein)
                        CaloricBreakdownItem("Fat", breakdown.percentFat)
                        CaloricBreakdownItem("Carbs", breakdown.percentCarbs)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaloricBreakdownItem(label: String, percentage: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

