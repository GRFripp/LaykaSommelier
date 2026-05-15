package com.example.laykasommelier

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.laykasommelier.network.RetrofitClient
import com.example.laykasommelier.viewModels.DrinkEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkEditFragment: Fragment() {
    private val viewModel: DrinkEditViewModel by viewModels()
    private lateinit var reviewAdapter: ReviewDrinkAdapter

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

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
        val ivDrinkImage = view.findViewById<ImageView>(R.id.ivDrinkImage)
        val btnLoadDrinkImage = view.findViewById<Button>(R.id.btnLoadDrinkImage)

        btnLoadDrinkImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // === Загрузка изображения (объединённая подписка на локальный uri и URL из состояния) ===
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.selectedImageUri, viewModel.state) { uri, state ->
                uri to state.imageUrl
            }.collect { (uri, imageUrl) ->
                if (uri != null) {
                    Glide.with(this@DrinkEditFragment)
                        .load(uri)
                        .centerCrop()
                        .into(ivDrinkImage)
                } else if (!imageUrl.isNullOrEmpty()) {
                    val fullUrl = "http://10.0.2.2:5169" +
                            (if (imageUrl.startsWith("/")) imageUrl else "/$imageUrl")
                    Glide.with(this@DrinkEditFragment)
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(ivDrinkImage)
                } else {
                    ivDrinkImage.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }

        // === Адаптер рецензий ===
        reviewAdapter = ReviewDrinkAdapter { reviewId ->
            val action = DrinkEditFragmentDirections.actionDrinkEditFragmentToReviewEditDialog(
                viewModel.drinkId, reviewId
            )
            findNavController().navigate(action)
        }
        rvReviews.layoutManager = LinearLayoutManager(requireContext())
        rvReviews.adapter = reviewAdapter

        // === Подписка на состояние напитка ===
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

        // === Слушатели ввода ===
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etType.doAfterTextChanged { viewModel.onTypeChanged(it) }
        etSubType.doAfterTextChanged { viewModel.onSubTypeChanged(it) }
        etCountry.doAfterTextChanged { viewModel.onCountryChanged(it) }
        etProducer.doAfterTextChanged { viewModel.onProducerChanged(it) }
        etAged.doAfterTextChanged { viewModel.onAgedChanged(it) }
        etAbv.doAfterTextChanged { viewModel.onAbvChanged(it) }

        // === Кнопка сохранения ===
        btnSaveDrink.setOnClickListener {
            viewModel.saveDrink()
        }

        // === Подписка на список рецензий ===
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.collect { reviewList ->
                reviewAdapter.submitList(reviewList)
            }
        }

        // === Кнопка добавления рецензии ===
        btnAddReview.setOnClickListener {
            val action = DrinkEditFragmentDirections
                .actionDrinkEditFragmentToReviewEditDialog(viewModel.drinkId, -1L)
            findNavController().navigate(action)
        }
    }
}