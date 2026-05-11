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
import com.example.laykasommelier.viewModels.DrinkListTypeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue


@AndroidEntryPoint
class DrinkCategoriesFragment: Fragment() {

    private val viewModel: DrinkListTypeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drink_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.drinkTypeListRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = DrinkTypeAdapter{clickedType ->
            val action = DrinkCategoriesFragmentDirections.actionDrinkCategoriesFragmentToDrinkListTypeSelectedFragment(clickedType)
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.drinkListTypes.collect {
                    list -> adapter.submitList(list)
            }
        }
    }
}