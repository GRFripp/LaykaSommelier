package com.example.laykasommelier

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.laykasommelier.data.local.pojo.IngredientEditState
@AndroidEntryPoint
class IngredientEditFragment: DialogFragment() {
    private val viewModel: IngredientEditViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_ingredient_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etIngredientName)
        val etAcidity = view.findViewById<EditText>(R.id.etAcidity)
        val etSugar = view.findViewById<EditText>(R.id.etSugarLevel)
        val etSearch = view.findViewById<EditText>(R.id.etSearchDescriptor)
        val flexboxCategories = view.findViewById<FlexboxLayout>(R.id.flexboxCategories)
        val flexboxDescriptors = view.findViewById<FlexboxLayout>(R.id.flexboxDescriptors)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                // Название (текстовое поле)
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                // Кислотность (числовое поле)
                if (etAcidity.text.toString() != state.acidity) {
                    val pos = etAcidity.selectionStart
                    etAcidity.setText(state.acidity)
                    etAcidity.setSelection(minOf(pos, state.acidity.length))
                }
                // Сладость (числовое поле)
                if (etSugar.text.toString() != state.sugarLevel) {
                    val pos = etSugar.selectionStart
                    etSugar.setText(state.sugarLevel)
                    etSugar.setSelection(minOf(pos, state.sugarLevel.length))
                }
                // Поисковая строка etSearch намеренно не обновляется – она только передаёт ввод во ViewModel,
                // а обратная установка привела бы к конфликту.
            }
        }
        // --- Слушатели ввода ---
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etAcidity.doAfterTextChanged { viewModel.onAcidityChanged(it) }
        etSugar.doAfterTextChanged { viewModel.onSugarChanged(it) }
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
            viewModel.state.collect { state ->
                updateDescriptorChips(flexboxDescriptors, state)
            }
        }
        // Подписка на отфильтрованный список (при изменении поиска/категории)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredDescriptors.collect { descriptors ->
                // Очищаем и перестраиваем чипы
                flexboxDescriptors.removeAllViews()
                val selectedIds = viewModel.state.value.selectedDescriptorIds
                descriptors.forEach { desc ->
                    val chip = Chip(flexboxDescriptors.context).apply {
                        text = desc.descriptorName
                        isCheckable = true
                        isCheckedIconEnabled = false
                        isChecked = selectedIds.contains(desc.descriptorId)
                        chipBackgroundColor = if (isChecked) {
                            ColorStateList.valueOf(Color.parseColor(desc.categoryColor))
                        } else {
                            ColorStateList.valueOf(Color.LTGRAY)
                        }
                        setTextColor(if (isChecked) Color.WHITE else Color.BLACK)
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.onDescriptorToggled(desc.descriptorId)
                        }
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