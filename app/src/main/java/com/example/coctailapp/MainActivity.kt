package com.example.coctailapp

import android.os.Bundle
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
import com.example.coctailapp.ui.theme.CoctailAppTheme

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
            "about_cocktail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktailName = backStackEntry.arguments?.getString("cocktailName") ?: "Unknown"
            AboutCocktailScreen(cocktailName)
        }
    }
}


@Composable
fun CocktailListScreen(onCocktailClick: (String) -> Unit) {
    val cocktails = listOf(
        "Mojito", "Martini", "Margarita", "Old Fashioned",
        "Daiquiri", "Negroni", "Piña Colada"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cocktails) { cocktail ->
            Text(
                text = cocktail,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCocktailClick(cocktail) }
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun AboutCocktailScreen(cocktailName: String) {
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
            text = cocktailName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CoctailAppTheme {
        CocktailListScreen { }
    }
}



