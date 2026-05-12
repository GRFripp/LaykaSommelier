package com.example.laykasommelier

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.entities.MakingMethod
import com.example.laykasommelier.viewModels.CocktailEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CocktailEditFragment : Fragment(){
    private val viewModel: CocktailEditViewModel by viewModels()
    private lateinit var ingredientLinkAdapter: CocktailIngredientLinkAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cocktail_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View-элементы
        val etName = view.findViewById<EditText>(R.id.etCocktailName)
        val etVolume = view.findViewById<EditText>(R.id.etVolume)
        val etAcidity = view.findViewById<EditText>(R.id.etAcidity)
        val etSugar = view.findViewById<EditText>(R.id.etSugar)
        val etAbv = view.findViewById<EditText>(R.id.etAbv)
        val etGlass = view.findViewById<EditText>(R.id.etGlass)
        val spinnerMethod = view.findViewById<Spinner>(R.id.spinnerMethod)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etAuthor = view.findViewById<EditText>(R.id.etAuthor)
        val etServing = view.findViewById<EditText>(R.id.etServing)
        val rvIngredients = view.findViewById<RecyclerView>(R.id.rvIngredients)
        val btnAddIngredient = view.findViewById<Button>(R.id.btnAddIngredient)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Спиннер для метода
        val methodList = mutableListOf<MakingMethod>()
        val methodAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinnerMethod.adapter = methodAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.makingMethods.collect { methods ->
                methodList.clear(); methodList.addAll(methods)
                methodAdapter.clear()
                methodAdapter.addAll(methods.map { "${it.makingMethodName} (${it.makingMethodDilution}" })
                methodAdapter.notifyDataSetChanged()
            }
        }
        spinnerMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                viewModel.onMakingMethodChanged(methodList.getOrNull(pos)?.makingMethodID ?: -1L)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Адаптер ингредиентов
        ingredientLinkAdapter = CocktailIngredientLinkAdapter { ingredientId ->
            viewModel.removeIngredient(ingredientId)
        }
        rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        rvIngredients.adapter = ingredientLinkAdapter

        // Заполнение полей из state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                setEditText(etName, state.name)
                setEditText(etVolume, state.volume)
                setEditText(etAcidity, state.acidity)
                setEditText(etSugar, state.sugarLevel)
                setEditText(etAbv, state.abv)
                setEditText(etGlass, state.glass)
                setEditText(etDescription, state.description)
                setEditText(etAuthor, state.author)
                setEditText(etServing, state.serving)
                if (state.makingMethodId != -1L) {
                    val idx = methodList.indexOfFirst { it.makingMethodID == state.makingMethodId }
                    if (idx >= 0 && spinnerMethod.selectedItemPosition != idx) spinnerMethod.setSelection(idx)
                }
            }
        }

        // Слушатели ввода
        etName.doAfterTextChanged { viewModel.onNameChanged(it) }
        etVolume.doAfterTextChanged { viewModel.onVolumeChanged(it) }
        etAcidity.doAfterTextChanged { viewModel.onAcidityChanged(it) }
        etSugar.doAfterTextChanged { viewModel.onSugarChanged(it) }
        etAbv.doAfterTextChanged { viewModel.onAbvChanged(it) }
        etGlass.doAfterTextChanged { viewModel.onGlassChanged(it) }
        etDescription.doAfterTextChanged { viewModel.onDescriptionChanged(it) }
        etAuthor.doAfterTextChanged { viewModel.onAuthorChanged(it) }
        etServing.doAfterTextChanged { viewModel.onServingChanged(it) }

        // Ингредиенты
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ingredientLinks.collect { items ->
                ingredientLinkAdapter.submitList(items)
            }
        }

        // Добавление ингредиента
        btnAddIngredient.setOnClickListener {
            showAddIngredientDialog()
        }

        // Сохранение
        btnSave.setOnClickListener { viewModel.saveCocktail() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveSuccess.collect {
                findNavController().popBackStack()
            }
        }
    }

    private fun setEditText(editText: EditText, text: String) {
        if (editText.text.toString() != text) {
            val pos = editText.selectionStart
            editText.setText(text)
            editText.setSelection(minOf(pos, text.length))
        }
    }

    private fun showAddIngredientDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val ingredients = viewModel.allIngredients.first()
            val names = ingredients.map { it.ingredientName }.toTypedArray()
            val ids = ingredients.map { it.ingredientID }.toLongArray()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Выберите ингредиент")
            builder.setItems(names) { dialog, which ->
                val selectedId = ids[which]
                val volumeInput = EditText(requireContext())
                volumeInput.hint = "Объём (мл)"
                volumeInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                val volumeDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Объём")
                    .setView(volumeInput)
                    .setPositiveButton("Добавить") { _, _ ->
                        val vol = volumeInput.text.toString().toDoubleOrNull() ?: 0.0
                        viewModel.addIngredient(selectedId, vol)
                    }
                    .setNegativeButton("Отмена", null)
                    .create()
                volumeDialog.show()
            }
            builder.setNegativeButton("Отмена", null)
            builder.create().show()
        }
    }
}