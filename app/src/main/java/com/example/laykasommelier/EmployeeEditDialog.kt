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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmployeeEditDialog: DialogFragment() {
    private val viewModel: EmployeeEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_employee_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI элементы
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val etName = view.findViewById<EditText>(R.id.etName)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinnerRole)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Настроим Spinner
        val roles = arrayOf("assistant", "bartender", "manager")
        val roleLabels = arrayOf("Помощник", "Бармен", "Менеджер")
        spinnerRole.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roleLabels)

        // Подписка на состояние (заполнение полей)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (etName.text.toString() != state.name) etName.setText(state.name)
                // Установим выбранную роль
                val roleIndex = roles.indexOf(state.role)
                if (roleIndex >= 0) spinnerRole.setSelection(roleIndex)
                // Пароль не показываем, но если нужно сбросить, оставляем пустым
            }
        }

        // Слушатели изменений
        etName.doAfterTextChanged { viewModel.onNameChanged(it.toString()) }
        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.onRoleChanged(roles[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        etPassword.doAfterTextChanged { viewModel.onPasswordChanged(it.toString()) }

        // Кнопки
        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { viewModel.save() }

        // Событие успешного сохранения – закрываем
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Задаём размеры диалога (по ширине 90% экрана)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}