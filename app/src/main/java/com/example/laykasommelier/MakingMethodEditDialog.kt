package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MakingMethodEditDialog: DialogFragment() {
    private val viewModel: MakingMethodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_making_method_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etMethodName)
        val etDilution = view.findViewById<EditText>(R.id.etDilution)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Подписка на состояние
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                if (etDilution.text.toString() != state.dilution) {
                    val pos = etDilution.selectionStart
                    etDilution.setText(state.dilution)
                    etDilution.setSelection(minOf(pos, state.dilution.length))
                }
            }
        }

        // Слушатели
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etDilution.doAfterTextChanged { viewModel.onDilutionChanged(it) }

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