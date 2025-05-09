package com.example.coctailapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
// Add this import
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import coil.compose.AsyncImage

import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coctailapp.network.Cocktail
import com.example.coctailapp.network.CocktailApiService
import com.example.coctailapp.network.CocktailDetails
import com.example.coctailapp.network.RetrofitInstance.apiService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coctailapp.ui.theme.CoctailAppTheme
import kotlinx.coroutines.delay
import kotlin.compareTo
import kotlin.sequences.ifEmpty


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoctailAppTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()

// Set up the navigation controller
    NavHost(navController, startDestination = "cocktail_list") {
        composable("cocktail_list") {
            CocktailListScreen { selectedCocktail ->
                navController.navigate("about_cocktail/${selectedCocktail}")
            }
        }
        composable(
            "about_cocktail/{idDrink}",
            arguments = listOf(navArgument("idDrink") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString("idDrink") ?: "Unknown"
            AboutCocktailScreen(cocktailId)
        }
    }
}


@Composable
fun CocktailListScreen(onCocktailClick: (String) -> Unit) {
    val context = LocalContext.current
    val cocktailsAlco by produceState<List<Cocktail>>(initialValue = emptyList()) {
        try {
            value =
                apiService.getCocktails("https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Alcoholic").drinks
            Log.d("CocktailList", "Fetched cocktails: $value")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch cocktails", Toast.LENGTH_SHORT).show()
            Log.e("CocktailList", "Error fetching cocktails", e)
        }
    }
    val cocktailsNonAlco by produceState<List<Cocktail>>(initialValue = emptyList()) {
        try {
            value =
                apiService.getCocktails("https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic").drinks
            Log.d("CocktailList", "Fetched cocktails: $value")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch cocktails", Toast.LENGTH_SHORT).show()
            Log.e("CocktailList", "Error fetching cocktails", e)
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Główna", "Alco", "Non Alco")

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
                        Text(
                            text = "",
                        )
                    }
                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            }
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTabIndex) {
                0 -> Text("Główna zawartość")
                1 -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cocktailsAlco) { cocktail ->
                            CocktailCard(cocktail, onCocktailClick)
                        }
                    }
                }

                2 -> {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        items(cocktailsNonAlco) { cocktail ->
//                            CocktailCard(cocktail, onCocktailClick)
//
//                        }
//                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cocktailsNonAlco) { cocktail ->
                            CocktailCard(cocktail, onCocktailClick)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun CocktailCard(x0: Cocktail, x1: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { x1(x0.idDrink) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = x0.strDrinkThumb,
                contentDescription = "Drink"
            )

            Text(
                text = x0.strDrink,
                style = MaterialTheme.typography.headlineSmall,

                fontSize = 16.sp,
                lineHeight = 18.sp
            )

        }
    }

}

@Composable
fun AboutCocktailScreen(cocktailId: String) {

    val context = LocalContext.current
    val cocktail by produceState<CocktailDetails?>(initialValue = null) {
        try {
            value =
                apiService.getCocktailDetails("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=${cocktailId}").drinks.firstOrNull()
            Log.d("CocktailDetails", "Fetched cocktail: $value")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch cocktail details", Toast.LENGTH_SHORT)
                .show()
        }
    }

    cocktail?.let { cocktail ->

        val ingredients = listOfNotNull(
            cocktail.strIngredient1,
            cocktail.strIngredient2,
            cocktail.strIngredient3,
            cocktail.strIngredient4,
            cocktail.strIngredient5,
            cocktail.strIngredient6,
            cocktail.strIngredient7,
            cocktail.strIngredient8
        ).filter { ingredient -> ingredient.isNotBlank() }

        Surface(
            //color = MaterialTheme.colorScheme.primary,
            //tonalElevation = 4.dp
        ) {
            Scaffold(
                floatingActionButton = { SmsFab(ingredients) },
                content = { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()) // Scroll na cały ekran
                    ) {
                        AsyncImage(
                            model = cocktail.strDrinkThumb,
                            contentDescription = "Drink"
                        )
                        Text(
                            text = "Szczegóły koktajlu:",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nazwa: ${cocktail.strDrink}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Składniki:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        ingredients.forEach { ingredient ->
                            Text(
                                text = "- $ingredient",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Instrukcje: ${cocktail.strInstructions}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column {
                            Text(text = "Minutnik")
                            TimerScreen()
                        }
                    }
                }
            )
        }
    } ?: Text(
        text = "Brak danych o koktajlu.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(24.dp)
    )
}

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val timeLeft by viewModel.timeLeft
    val isRunning by viewModel.isRunning

    var minutes by rememberSaveable { mutableStateOf("") }
    var seconds by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isRunning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minutes,
                    onValueChange = {
                        if ((it.isEmpty() || it.all { char -> char.isDigit() }) && it.length <= 2) {
                            minutes = it
                        }
                    },
                    label = { Text("Minuty") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = ":",
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp)
                )

                OutlinedTextField(
                    value = seconds,
                    onValueChange = {
                        if ((it.isEmpty() || it.all { char -> char.isDigit() }) && it.length <= 2) {
                            seconds = it
                        }
                    },
                    label = { Text("Sekundy") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val totalSeconds =
                                (minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)
                            viewModel.startTimer(totalSeconds)
                        }
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        } else {

            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            Text(
                text = String.format("%d:%02d", minutes, seconds),
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    val totalSeconds =
                        (minutes.toIntOrNull() ?: 0) * 60 + (seconds.toIntOrNull() ?: 0)
                    viewModel.startTimer(totalSeconds)
                }
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start")
            }
            IconButton(
                onClick = {
                    viewModel.stopTimer()
                    // Convert timeLeft to minutes and seconds and update input fields
                    minutes = (timeLeft / 60).toString()
                    seconds = (timeLeft % 60).toString().padStart(2, '0')
                }
            ) {
                Icon(Icons.Default.Pause, contentDescription = "Pause")
            }
            IconButton(
                onClick = {
                    viewModel.resetTimer()
                    minutes = ""  // Clear instead of setting to "0"
                    seconds = ""  // Clear instead of setting to "0"
                }
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
            }
        }
    }
}

@Composable
fun SmsFab(ingredients: List<String>) {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            val phoneNumber = "123456789" // ← wpisz numer
            val message = ingredients.joinToString(", ")
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Default.Send, contentDescription = "Wyślij SMS")
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoctailAppTheme {
        CocktailListScreen { }
    }
}



