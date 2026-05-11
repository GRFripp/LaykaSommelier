package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.viewModels.DrinkEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkEditFragment: Fragment() {
    private val viewModel: DrinkEditViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewDrinkAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_drink_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etDrinkName)
        val etType = view.findViewById<EditText>(R.id.etDrinkType)
        val etSubType = view.findViewById<EditText>(R.id.etDrinkSubType)
        val etCountry = view.findViewById<EditText>(R.id.etDrinkCountry)
        val etProducer = view.findViewById<EditText>(R.id.etDrinkProducer)
        val etAged = view.findViewById<EditText>(R.id.etDrinkAged)
        val etAbv = view.findViewById<EditText>(R.id.etDrinkAbv)
        val btnSaveDrink = view.findViewById<Button>(R.id.btnSaveDrink)
        val rvReviews = view.findViewById<RecyclerView>(R.id.rvReviews)
        val btnAddReview = view.findViewById<Button>(R.id.btnAddReview)

        reviewAdapter = ReviewDrinkAdapter { reviewId ->
            val action = DrinkEditFragmentDirections.actionDrinkEditFragmentToReviewEditDialog(
                viewModel.drinkId, reviewId
            )
            findNavController().navigate(action)
        }
        rvReviews.layoutManager = LinearLayoutManager(requireContext())
        rvReviews.adapter = reviewAdapter

        // Подписка на состояние напитка
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                if (etType.text.toString() != state.type) {
                    val pos = etType.selectionStart
                    etType.setText(state.type)
                    etType.setSelection(minOf(pos, state.type.length))
                }
                if (etSubType.text.toString() != state.subType) {
                    val pos = etSubType.selectionStart
                    etSubType.setText(state.subType)
                    etSubType.setSelection(minOf(pos, state.subType.length))
                }
                if (etCountry.text.toString() != state.country) {
                    val pos = etCountry.selectionStart
                    etCountry.setText(state.country)
                    etCountry.setSelection(minOf(pos, state.country.length))
                }
                if (etProducer.text.toString() != state.producer) {
                    val pos = etProducer.selectionStart
                    etProducer.setText(state.producer)
                    etProducer.setSelection(minOf(pos, state.producer.length))
                }
                if (etAged.text.toString() != state.aged) {
                    val pos = etAged.selectionStart
                    etAged.setText(state.aged)
                    etAged.setSelection(minOf(pos, state.aged.length))
                }
                if (etAbv.text.toString() != state.abv) {
                    val pos = etAbv.selectionStart
                    etAbv.setText(state.abv)
                    etAbv.setSelection(minOf(pos, state.abv.length))
                }
            }
        }
        btnSaveDrink.setOnClickListener {
            viewModel.saveDrink()
        }
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etType.doAfterTextChanged { viewModel.onTypeChanged(it) }
        etSubType.doAfterTextChanged { viewModel.onSubTypeChanged(it) }
        etCountry.doAfterTextChanged { viewModel.onCountryChanged(it) }
        etProducer.doAfterTextChanged { viewModel.onProducerChanged(it) }
        etAged.doAfterTextChanged { viewModel.onAgedChanged(it) }
        etAbv.doAfterTextChanged { viewModel.onAbvChanged(it) }

        // Подписка на список рецензий
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.collect { reviewList ->
                reviewAdapter.submitList(reviewList)
            }
        }


// При добавлении новой рецензии
        btnAddReview.setOnClickListener {
            val action = DrinkEditFragmentDirections
                .actionDrinkEditFragmentToReviewEditDialog(viewModel.drinkId, -1L)
            findNavController().navigate(action)
        }
    }
}