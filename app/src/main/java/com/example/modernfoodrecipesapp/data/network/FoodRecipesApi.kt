package com.example.modernfoodrecipesapp.data.network

import com.example.modernfoodrecipesapp.model.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipesApi  {
    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ):Response<FoodRecipe>
}