package com.example.modernfoodrecipesapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.modernfoodrecipesapp.R
import com.facebook.shimmer.ShimmerFrameLayout


class RecipesFragment : Fragment() {
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipes, container, false)

        shimmerFrameLayout = view.findViewById(R.id.shimmer_recyclerview)


        shimmerFrameLayout.startShimmer()
        return view

    }

}