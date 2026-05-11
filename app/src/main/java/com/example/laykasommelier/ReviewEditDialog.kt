package com.example.laykasommelier

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.laykasommelier.data.local.entities.Source
import com.example.laykasommelier.viewModels.ReviewEditViewModel
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ReviewEditDialog: DialogFragment() {

    private val viewModel: ReviewEditViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_review_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerSource = view.findViewById<Spinner>(R.id.spinnerSource)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)
        val etSearch = view.findViewById<EditText>(R.id.etSearchDescriptor)
        val flexboxCategories = view.findViewById<FlexboxLayout>(R.id.flexboxCategories)
        val flexboxDescriptors = view.findViewById<FlexboxLayout>(R.id.flexboxDescriptors)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // === Источники (Spinner) ===
        val sourceList = mutableListOf<Source>()
        val sourceAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerSource.adapter = sourceAdapter

        // Единая подписка на источники и выбранный sourceId
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.sources,
                viewModel.state.map { it.sourceId }
            ) { sources, sourceId -> sources to sourceId }
                .collect { (sources, sourceId) ->
                    // Обновляем список источников
                    sourceList.clear()
                    sourceList.addAll(sources)
                    sourceAdapter.clear()
                    sourceAdapter.addAll(sources.map { it.sourceName })
                    sourceAdapter.notifyDataSetChanged()

                    // Устанавливаем выбранный пункт, если sourceId задан
                    if (sourceId != -1L) {
                        val index = sourceList.indexOfFirst { it.sourceID == sourceId }
                        if (index >= 0 && spinnerSource.selectedItemPosition != index) {
                            spinnerSource.setSelection(index)
                        }
                    }
                }
        }

        spinnerSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val selected = sourceList.getOrNull(pos)
                if (selected != null) viewModel.onSourceChanged(selected.sourceID)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // === Поля ввода ===
        etUrl.doAfterTextChanged { viewModel.onUrlChanged(it) }
        etSearch.doAfterTextChanged { viewModel.onSearchChanged(it) }

        // Подписка на URL (будет заполнять поле при редактировании)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.map { it.url }.collect { url ->
                if (etUrl.text.toString() != url) {
                    val pos = etUrl.selectionStart
                    etUrl.setText(url)
                    etUrl.setSelection(minOf(pos, url.length))
                }
            }
        }

        // === Чипы категорий (без изменений) ===
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                flexboxCategories.removeAllViews()
                categories.forEach { category ->
                    val chip = Chip(flexboxCategories.context).apply {
                        text = category.descriptorCategoryName
                        isCheckable = true
                        isCheckedIconEnabled = false
                        chipBackgroundColor =
                            ColorStateList.valueOf(Color.parseColor(category.descriptorCategoryColor))
                        setTextColor(Color.WHITE)
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.onCategoryFilterSelected(if (isChecked) category.descriptorCategoryID else null)
                        }
                    }
                    flexboxCategories.addView(chip)
                }
            }
        }

        // === Чипы дескрипторов (реактивно на выбор) ===
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.descriptorsWithCategories,
                viewModel.state.map { it.selectedDescriptorIds }
            ) { descriptors, selectedIds -> descriptors to selectedIds }
                .collect { (descriptors, selectedIds) ->
                    flexboxDescriptors.removeAllViews()
                    descriptors.forEach { desc ->
                        val chip = Chip(flexboxDescriptors.context).apply {
                            text = desc.descriptorName
                            isCheckable = true
                            isCheckedIconEnabled = false
                            setOnCheckedChangeListener(null)
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

        // === Кнопки ===
        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { viewModel.save() }
        if (viewModel.reviewId != -1L) btnDelete.visibility = View.VISIBLE
        btnDelete.setOnClickListener { viewModel.deleteReview() }

        // === Закрытие при успехе ===
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect {
                dismiss()
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