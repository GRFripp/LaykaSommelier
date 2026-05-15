package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.EmployeeRole
import com.example.laykasommelier.viewModels.DrinkListTypeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue


@AndroidEntryPoint
class DrinkCategoriesFragment: Fragment() {

    private val viewModel: DrinkListTypeViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager
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
            val navController = androidx.navigation.fragment.NavHostFragment.findNavController(this@DrinkCategoriesFragment)
            val action = DrinkCategoriesFragmentDirections.actionDrinkCategoriesFragmentToDrinkListTypeSelectedFragment(clickedType)
            navController.navigate(action)
        }
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.drinkListTypes.collect {
                    list -> adapter.submitList(list)
            }
        }
        val fab = view.findViewById<FloatingActionButton>(R.id.fabDrinkType)
        fab.setOnClickListener {
            val action = DrinkCategoriesFragmentDirections.actionDrinkCategoriesFragmentToDrinkEditFragment(-1L)
            findNavController().navigate(action)
        }
        val role = sessionManager.getRole()
        fab.visibility = if (role == EmployeeRole.BARTENDER || role == EmployeeRole
                .MANAGER) View.VISIBLE else View.GONE
    }
}