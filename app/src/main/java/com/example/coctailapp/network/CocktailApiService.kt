package com.example.coctailapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {
    @GET("filter.php")
    suspend fun getCocktails(@Query("c") category: String): CocktailResponse
}

data class CocktailResponse(val drinks: List<Cocktail>)
data class Cocktail(val strDrink: String)