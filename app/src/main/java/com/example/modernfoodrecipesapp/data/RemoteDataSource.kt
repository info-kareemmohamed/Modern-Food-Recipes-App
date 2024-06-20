package com.example.modernfoodrecipesapp.data

import com.example.modernfoodrecipesapp.data.network.FoodRecipesApi
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {


    suspend fun getRecipes(queries: Map<String, String>): Response<Object> {
        return foodRecipesApi.getRecipes(queries = queries)
    }
}