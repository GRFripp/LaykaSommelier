package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkListTypeSelectedFragment: Fragment() {

    //private val args: DrinkListTypeSelectedFragmentArgs by navArgs()

    val viewModel: DrinkListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drink_selected_type,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DrinkListSelectedAdapter { drinkId ->
            //заглушка
        }
        val rv : RecyclerView = view.findViewById<RecyclerView>(R.id.drinkListRV)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.drinkPreview.collect { list ->
                adapter.submitList(list)
            }
        }

    }
}