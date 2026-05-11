package com.example.laykasommelier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.laykasommelier.data.local.entities.DescriptorCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class DescriptorEditDialog : DialogFragment() {
    private val viewModel: DescriptorEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_descriptor_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etDescriptorName)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Адаптер для Spinner
        val categoryList = mutableListOf<DescriptorCategory>()
        val categoryAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCategory.adapter = categoryAdapter

        // Загружаем категории
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                categoryList.clear()
                categoryList.addAll(categories)
                categoryAdapter.clear()
                categoryAdapter.addAll(categories.map { it.descriptorCategoryName })
                categoryAdapter.notifyDataSetChanged()
            }
        }

        // Подписка на состояние
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                val index = categoryList.indexOfFirst { it.descriptorCategoryID == state.categoryId }
                if (index >= 0) spinnerCategory.setSelection(index)
            }
        }

        // Слушатели
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCat = categoryList.getOrNull(position)
                if (selectedCat != null) {
                    viewModel.onCategorySelected(selectedCat.descriptorCategoryID)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { viewModel.save() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect { dismiss() }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}