package com.example.modernfoodrecipesapp

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipes {
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    )
}