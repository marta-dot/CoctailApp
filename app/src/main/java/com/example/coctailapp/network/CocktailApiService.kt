package com.example.coctailapp.network

import retrofit2.http.GET
import retrofit2.http.Url

interface CocktailApiService {
    @GET
    suspend fun getCocktails(@Url url: String): CocktailResponse
    @GET
    suspend fun getCocktailDetails(@Url url: String): DetailsResponse
    }


data class CocktailResponse(val drinks: List<Cocktail>)
data class DetailsResponse(val drinks: List<CocktailDetails>)
data class Cocktail(
    val idDrink: String,
    val strDrink: String
)
data class CocktailDetails(
    val idDrink: String,
    val strDrink: String,
    val strInstructions: String,
    val strDrinkThumb: String,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?
)