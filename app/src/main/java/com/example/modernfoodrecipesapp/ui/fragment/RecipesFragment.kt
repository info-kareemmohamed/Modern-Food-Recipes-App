package com.example.modernfoodrecipesapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.adapter.RecipesAdapter
import com.example.modernfoodrecipesapp.util.Constant
import com.example.modernfoodrecipesapp.util.Constant.Companion.API_KEY
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_API_KEY
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_DIET
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_FILL_INGREDIENTS
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_NUMBER
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_TYPE
import com.example.modernfoodrecipesapp.util.NetworkResult
import com.example.modernfoodrecipesapp.viewmodel.MainViewModel
import com.example.modernfoodrecipesapp.viewmodel.RecipesViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private lateinit var recipesViewModel: RecipesViewModel
    private lateinit var mainViewModel: MainViewModel
    private val myAdapter by lazy { RecipesAdapter() }
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recipes, container, false)
        setupRecyclerView()
        loadData()
        return view

    }


    private fun setupRecyclerView() {
        recyclerView = view.findViewById<RecyclerView>(R.id.recipes_recyclerview)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        startShimmer()
    }


    private fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = "50"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    private fun loadData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.let { myAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    stopShimmer()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
                is NetworkResult.Loading -> {
                    startShimmer()
                }
            }
        }
    }

    private fun startShimmer() {
        shimmerFrameLayout = view.findViewById(R.id.shimmer_recyclerview)
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
    }

    private fun stopShimmer() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

}