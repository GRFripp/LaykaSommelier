package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.AdminEntityType
import com.example.laykasommelier.data.local.pojo.AdminListItem
import com.example.laykasommelier.viewModels.AdminViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AdminListFragment: Fragment() {
    private val viewModel: AdminViewModel by viewModels()


    private val allTypes: List<AdminEntityType> = AdminEntityType.values().toList()
    private val entityLabels: List<String> = allTypes.map { it.label }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNext = view.findViewById<ImageButton>(R.id.btnNext)
        val btnPrev = view.findViewById<ImageButton>(R.id.btnPrev)
        val tvCurrent = view.findViewById<TextView>(R.id.tvCurrent)
        val tvNext = view.findViewById<TextView>(R.id.tvNext)
        val tvPrev = view.findViewById<TextView>(R.id.tvPrev)

        val adapter = AdminRVAdapter{ item ->
            when (item) {
                is AdminListItem.ALEmployee -> {
                    val action = AdminListFragmentDirections.actionAdminListFragment2ToEmployeeEditDialog(item.id)
                    findNavController().navigate(action)
                }
                is AdminListItem.ALMakingMethod -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToMakingMethodEditDialog(item.id)
                    findNavController().navigate(action)
                }
                is AdminListItem.ALIngredient -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToIngredientEditDialog(item.id)
                    findNavController().navigate(action)
                }
                is AdminListItem.ALDescriptorCategory -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToDescriptorCategoryEditDialog(item.id)
                    findNavController().navigate(action)
                }
                is AdminListItem.ALDescriptor -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToDescriptorEditDialog(item.id)
                    findNavController().navigate(action)
                }
        }
        }
        val rv : RecyclerView = view.findViewById(R.id.adminRV)
        rv.layoutManager= LinearLayoutManager(requireContext())
        rv.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentType.collect { type ->
                val index = AdminEntityType.values().indexOf(type)
                updateCarousel(index,tvCurrent,tvNext,tvPrev)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.items.collect { list ->
                adapter.submitList(list)
            }
        }
        btnPrev.setOnClickListener { shiftType(-1) }
        btnNext.setOnClickListener { shiftType(1) }
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddAdmin)
        fabAdd.setOnClickListener {
            val type = viewModel.currentType.value
            when (type) {
                AdminEntityType.EMPLOYEES -> {
                    val action = AdminListFragmentDirections.actionAdminListFragment2ToEmployeeEditDialog(-1L)
                    findNavController().navigate(action)
                }
                AdminEntityType.INGREDIENTS -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToIngredientEditDialog(-1L)
                    findNavController().navigate(action)
                }
                AdminEntityType.DESCRIPTORS -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToDescriptorEditDialog(-1L)
                    findNavController().navigate(action)
                }
                AdminEntityType.DESCRIPTORCATEGORIES -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToDescriptorCategoryEditDialog(-1L)
                    findNavController().navigate(action)
                }
                AdminEntityType.MAKINGMETHODS -> {
                    val action = AdminListFragmentDirections.actionAdminListFragmentToMakingMethodEditDialog(-1L)
                    findNavController().navigate(action)
                }
            }
        }
    }
    private fun updateCarousel(currentIndex: Int, tvCurrent:TextView,tvNext:TextView,tvPrev: TextView){
        if (allTypes.isEmpty()) return
        val prevIndex = if (currentIndex == 0) allTypes.size - 1 else currentIndex - 1
        val nextIndex = (currentIndex + 1) % allTypes.size

        tvCurrent.text = entityLabels[currentIndex]
        tvPrev.text = entityLabels[prevIndex]
        tvNext.text = entityLabels[nextIndex]

        tvCurrent.alpha = 1f
        tvPrev.alpha = 0.4f
        tvNext.alpha = 0.4f
    }
    private fun shiftType(step: Int) {
        if (allTypes.isEmpty()) return
        val currentIndex = allTypes.indexOf(viewModel.currentType.value)
        val newIndex = ((currentIndex + step) % allTypes.size + allTypes.size) % allTypes.size
        viewModel.onEntityTypeChanged(allTypes[newIndex])
    }
}