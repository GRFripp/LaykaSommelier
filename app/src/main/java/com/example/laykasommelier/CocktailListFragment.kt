package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CocktailListFragment: Fragment() {

    val viewModel: CocktailListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cocktail_list_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CocktailListAdapter{ cocktailId ->
            val action = CocktailListFragmentDirections.actionCocktailListFragmentToCocktailDetailFragment2(cocktailId)
            findNavController().navigate(action)
        }
        val rv: RecyclerView = view.findViewById(R.id.cocktailListRV)
        rv.adapter = adapter
        rv.layoutManager  = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cocktailPreviews.collect { list ->
                adapter.submitList(list)
            }
        }

    }
}