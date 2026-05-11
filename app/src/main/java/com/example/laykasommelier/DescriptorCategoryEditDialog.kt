package com.example.laykasommelier

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.laykasommelier.viewModels.DescriptorCategoryEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class DescriptorCategoryEditDialog: DialogFragment() {
    private val viewModel: DescriptorCategoryEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_category_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.etCategoryName)
        val etColorHex = view.findViewById<EditText>(R.id.etColorHex)
        val viewColorPreview = view.findViewById<View>(R.id.viewColorPreview)
        val btnPickColor = view.findViewById<Button>(R.id.btnPickColor)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Подписка на состояние
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                // Название категории
                if (etName.text.toString() != state.name) {
                    val pos = etName.selectionStart
                    etName.setText(state.name)
                    etName.setSelection(minOf(pos, state.name.length))
                }
                // HEX-цвет (ввод) – обновляем только при реальном отличии
                if (etColorHex.text.toString() != state.color) {
                    val pos = etColorHex.selectionStart
                    etColorHex.setText(state.color)
                    etColorHex.setSelection(minOf(pos, state.color.length))
                }
                // Превью цвета обновляем всегда (это View)
                try {
                    viewColorPreview.setBackgroundColor(Color.parseColor(state.color))
                } catch (_: Exception) {
                    viewColorPreview.setBackgroundColor(Color.GRAY)
                }
            }
        }

        // Слушатели ввода
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etColorHex.doAfterTextChanged { hex ->
            if (hex.isNotBlank() && hex.length == 7 && hex.startsWith("#")) {
                viewModel.onColorChanged(hex)
                try {
                    viewColorPreview.setBackgroundColor(Color.parseColor(hex))
                } catch (_: Exception) {}
            }
        }

        // Кнопка вызова палитры (открывает диалог с сеткой)
        btnPickColor.setOnClickListener {
            showColorPaletteDialog { selectedColor ->
                etColorHex.setText(selectedColor)
                viewModel.onColorChanged(selectedColor)
                viewColorPreview.setBackgroundColor(Color.parseColor(selectedColor))
            }
        }

        btnCancel.setOnClickListener { dismiss() }
        btnSave.setOnClickListener { viewModel.save() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect { dismiss() }
        }
    }

    // Простая палитра из часто используемых цветов
    private fun showColorPaletteDialog(onColorSelected: (String) -> Unit) {
        val colors = arrayOf(
            "#FF5733", "#33FF57", "#3357FF", "#FF33A1",
            "#FFD700", "#8A2BE2", "#00FFFF", "#FF4500",
            "#7CFC00", "#DC143C", "#00FA9A", "#FF6347"
        )

        val gridLayout = GridLayout(requireContext()).apply {
            rowCount = 3
            columnCount = 4
        }

        colors.forEach { colorStr ->
            val colorView = View(requireContext()).apply {
                setBackgroundColor(Color.parseColor(colorStr))
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 100
                    height = 100
                    setMargins(8, 8, 8, 8)
                }
                setOnClickListener {
                    onColorSelected(colorStr)
                    // Закрыть диалог с палитрой (этот dialog – отдельный AlertDialog)
                    (it.parent.parent as? Dialog)?.dismiss()
                }
            }
            gridLayout.addView(colorView)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите цвет")
            .setView(gridLayout)
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}