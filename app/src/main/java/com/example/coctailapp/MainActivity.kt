package com.example.coctailapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coctailapp.network.Cocktail
import com.example.coctailapp.network.CocktailApiService
import com.example.coctailapp.network.CocktailDetails
import com.example.coctailapp.network.RetrofitInstance.apiService
import com.example.coctailapp.ui.theme.CoctailAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

@Composable
fun MainApp() {
    val navController = rememberNavController()

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
    val cocktails by produceState<List<Cocktail>>(initialValue = emptyList()) {
        try {
            value = apiService.getCocktails("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail").drinks
            Log.d("CocktailList", "Fetched cocktails: $value")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch cocktails", Toast.LENGTH_SHORT).show()
            Log.e("CocktailList", "Error fetching cocktails", e)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cocktails) { cocktail ->
            Text(
                text = cocktail.strDrink,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCocktailClick(cocktail.idDrink) }
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun AboutCocktailScreen(cocktailId: String) {

    val context = LocalContext.current
    val cocktail by produceState<CocktailDetails?>(initialValue = null) {
        try {
            value = apiService.getCocktailDetails("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=${cocktailId}").drinks.firstOrNull()
            Log.d("CocktailDetails", "Fetched cocktail: $value")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch cocktail details", Toast.LENGTH_SHORT).show()
        }
    }

    cocktail?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Szczegóły koktajlu:",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nazwa: ${it.strDrink}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Instrukcje: ${it.strInstructions}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } ?: Text(
        text = "Brak danych o koktajlu.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoctailAppTheme {
        CocktailListScreen { }
    }
}



