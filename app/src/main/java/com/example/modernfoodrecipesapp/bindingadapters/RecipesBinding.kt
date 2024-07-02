package com.example.modernfoodrecipesapp.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.modernfoodrecipesapp.data.database.RecipesEntity
import com.example.modernfoodrecipesapp.model.FoodRecipe
import com.example.modernfoodrecipesapp.util.NetworkResult


class RecipesBinding {

    companion object {

        @BindingAdapter("imageReadApiResponse", "imageReadDatabase", requireAll = true)
        @JvmStatic
        fun errorImageViewVisibility(
            imageView: ImageView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            if (apiResponse is NetworkResult.Error && database.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
            } else if (apiResponse is NetworkResult.Loading) {
                imageView.visibility = View.INVISIBLE
            } else if (apiResponse is NetworkResult.Success) {
                imageView.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("textReadApiResponse", "textReadDatabase", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(
            textView: TextView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            if (apiResponse is NetworkResult.Error && database.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            } else if (apiResponse is NetworkResult.Loading) {
                textView.visibility = View.INVISIBLE
            } else if (apiResponse is NetworkResult.Success) {
                textView.visibility = View.INVISIBLE
            }
        }

    }

}