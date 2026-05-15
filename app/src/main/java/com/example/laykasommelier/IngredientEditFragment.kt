package com.example.laykasommelier

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.laykasommelier.data.local.pojo.editstates.IngredientEditState
import com.example.laykasommelier.viewModels.IngredientEditViewModel
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class IngredientEditFragment: DialogFragment() {
    private val viewModel: IngredientEditViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_ingredient_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etIngredientName)
        val etAcidity = view.findViewById<EditText>(R.id.etAcidity)
        val etSugar = view.findViewById<EditText>(R.id.etSugarLevel)
        val etAbv = view.findViewById<EditText>(R.id.etAbv)
        val etSearch = view.findViewById<EditText>(R.id.etSearchDescriptor)
        val flexboxCategories = view.findViewById<FlexboxLayout>(R.id.flexboxCategories)
        val flexboxDescriptors = view.findViewById<FlexboxLayout>(R.id.flexboxDescriptors)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnLoadImage = view.findViewById<Button>(R.id.btnLoadIngredientImage)
        val ivIngredientImage = view.findViewById<ImageView>(R.id.ivIngredientImage)

        btnLoadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // === Загрузка изображения (реакция на локальный выбор и на URL из состояния) ===
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.selectedImageUri, viewModel.state) { uri, state ->
                uri to state.imageUrl
            }.collect { (uri, imageUrl) ->
                if (uri != null) {
                    Glide.with(this@IngredientEditFragment)
                        .load(uri)
                        .centerCrop()
                        .into(ivIngredientImage)
                } else if (!imageUrl.isNullOrEmpty()) {
                    val fullUrl = "http://10.0.2.2:5169" +
                            (if (imageUrl.startsWith("/")) imageUrl else "/$imageUrl")
                    Glide.with(this@IngredientEditFragment)
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(ivIngredientImage)
                } else {
                    ivIngredientImage.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }

        // === Подписка на состояние (текстовые поля + ABV) ===
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                if (etAcidity.text.toString() != state.acidity) {
                    val pos = etAcidity.selectionStart
                    etAcidity.setText(state.acidity)
                    etAcidity.setSelection(minOf(pos, state.acidity.length))
                }
                if (etSugar.text.toString() != state.sugarLevel) {
                    val pos = etSugar.selectionStart
                    etSugar.setText(state.sugarLevel)
                    etSugar.setSelection(minOf(pos, state.sugarLevel.length))
                }
                // === ДОБАВЛЕНО обновление ABV ===
                if (etAbv.text.toString() != state.abv) {
                    val pos = etAbv.selectionStart
                    etAbv.setText(state.abv)
                    etAbv.setSelection(minOf(pos, state.abv.length))
                }
            }
        }

        // --- Слушатели ввода ---
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etAcidity.doAfterTextChanged { viewModel.onAcidityChanged(it) }
        etSugar.doAfterTextChanged { viewModel.onSugarChanged(it) }
        etAbv.doAfterTextChanged { viewModel.onAbvChanged(it) }   // === ДОБАВЛЕНО ===
        etSearch.doAfterTextChanged { viewModel.onSearchChanged(it) }

        // --- Категории (чипы для фильтра) ---
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                flexboxCategories.removeAllViews()
                categories.forEach { category ->
                    val chip = Chip(flexboxCategories.context).apply {
                        text = category.descriptorCategoryName
                        isCheckable = true
                        isCheckedIconEnabled = false
                        chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(category.descriptorCategoryColor))
                        setTextColor(Color.WHITE)
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.onCategoryFilterSelected(if (isChecked) category.descriptorCategoryID else null)
                        }
                    }
                    flexboxCategories.addView(chip)
                }
            }
        }

        // --- Дескрипторы (фильтрованные чипы) ---
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.filteredDescriptors,
                viewModel.state.map { it.selectedDescriptorIds }
            ) { descriptors, selectedIds -> Pair(descriptors, selectedIds) }
                .collect { (descriptors, selectedIds) ->
                    flexboxDescriptors.removeAllViews()
                    for (desc in descriptors) {
                        val checked = selectedIds.contains(desc.descriptorId)
                        val chip = Chip(flexboxDescriptors.context)
                        chip.text = desc.descriptorName
                        chip.isCheckable = true
                        chip.isCheckedIconEnabled = false
                        chip.isChecked = checked
                        chip.chipBackgroundColor = if (checked) {
                            ColorStateList.valueOf(Color.parseColor(desc.categoryColor))
                        } else {
                            ColorStateList.valueOf(Color.LTGRAY)
                        }
                        chip.setTextColor(if (checked) Color.WHITE else Color.BLACK)
                        chip.setOnCheckedChangeListener { _, isNowChecked ->
                            viewModel.onDescriptorToggled(desc.descriptorId)
                        }
                        flexboxDescriptors.addView(chip)
                    }
                }
        }

        // --- Кнопки ---
        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { viewModel.save() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect { dismiss() }
        }
    }

    private fun updateDescriptorChips(container: FlexboxLayout, state: IngredientEditState) {
        // Обновляем checked state у существующих чипов
        val selectedIds = state.selectedDescriptorIds
        (0 until container.childCount).forEach { i ->
            val chip = container.getChildAt(i) as? Chip
            chip?.let {
                val id = it.tag as? Long
                if (id != null) {
                    it.isChecked = selectedIds.contains(id)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}