package com.example.modernfoodrecipesapp.ui.fragment.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.adapter.RecipesAdapter
import com.example.modernfoodrecipesapp.databinding.FragmentRecipesBinding
import com.example.modernfoodrecipesapp.util.Constant.Companion.API_KEY
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_API_KEY
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_DIET
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_FILL_INGREDIENTS
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_NUMBER
import com.example.modernfoodrecipesapp.util.Constant.Companion.QUERY_TYPE
import com.example.modernfoodrecipesapp.util.NetworkListener
import com.example.modernfoodrecipesapp.util.NetworkResult
import com.example.modernfoodrecipesapp.util.observeOnce
import com.example.modernfoodrecipesapp.viewmodel.MainViewModel
import com.example.modernfoodrecipesapp.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipesViewModel: RecipesViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var networkListener: NetworkListener
    private val myAdapter by lazy { RecipesAdapter() }


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
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner, {
            recipesViewModel.backOnline = it
        })
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }
        return binding.root

    }


    private fun setupRecyclerView() {
        binding.recipesRecyclerview.adapter = myAdapter
        binding.recipesRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        startShimmer()
    }


    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()&& !args.backFromBottomSheet) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    myAdapter.setData(database[0].foodRecipe)
                    stopShimmer()
                } else {
                    loadData()
                }
            })
        }
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
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    stopShimmer()
                    response.data?.let { myAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    stopShimmer()
                    loadDataFromCache()
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

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    myAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }

    private fun startShimmer() {
        binding.shimmerRecyclerview.visibility = View.VISIBLE
        binding.shimmerRecyclerview.startShimmer()
    }

    private fun stopShimmer() {
        binding.shimmerRecyclerview.stopShimmer()
        binding.shimmerRecyclerview.visibility = View.GONE
        binding.recipesRecyclerview.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}