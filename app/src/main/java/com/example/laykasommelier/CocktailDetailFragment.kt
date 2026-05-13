package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.viewModels.CocktailDetailViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CocktailDetailFragment : Fragment() {

    private val viewModel: CocktailDetailViewModel by viewModels()
    private lateinit var ingredientAdapter: CocktailIngredientAdapter
    private val args: CocktailDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.cocktail_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка «Редактировать»
        val btnEdit = view.findViewById<Button>(R.id.cocktailEditBtn)
        btnEdit.setOnClickListener {
            val action = CocktailDetailFragmentDirections
                .actionCocktailDetailFragmentToCocktailEditFragment(args.cocktailDetailId)
            findNavController().navigate(action)
        }

        // Инициализация View
        val tvName = view.findViewById<TextView>(R.id.cocktailDetailName)
        val tvVolume = view.findViewById<TextView>(R.id.cocktailDetailVolume)
        val tvAcidity = view.findViewById<TextView>(R.id.cocktailDetailAcidity)
        val tvSugar = view.findViewById<TextView>(R.id.cocktailDetailSugar)
        val tvAbv = view.findViewById<TextView>(R.id.cocktailDetailAbv)
        val tvGlass = view.findViewById<TextView>(R.id.cocktailDetailGlass)
        val tvMethod = view.findViewById<TextView>(R.id.cocktailDetailMethod)
        val tvDescription = view.findViewById<TextView>(R.id.cocktailDetailDescription)
        val tvAuthor = view.findViewById<TextView>(R.id.cocktailDetailAuthor)
        val tvServing = view.findViewById<TextView>(R.id.cocktailDetailServing)
        val rvIngredients = view.findViewById<RecyclerView>(R.id.cocktailIngredientsRV)

        // Адаптер ингредиентов
        ingredientAdapter = CocktailIngredientAdapter { ingredientId ->
            // Пока без действия, можно будет добавить детали ингредиента
        }
        rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        rvIngredients.adapter = ingredientAdapter

        // Подписка на данные коктейля
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cocktail.collect { cocktail ->
                cocktail?.let {
                    tvName.text = it.cocktailName
                    tvVolume.text = "${it.cocktailVolume} мл"
                    tvAcidity.text = "pH ${it.cocktailAcidity}"
                    tvSugar.text = "${it.cocktailSugarLevel} г/100мл"
                    tvAbv.text = "${it.cocktailAbv}%"
                    tvGlass.text = it.cocktailGlass
                    tvDescription.text = it.cocktailDescription
                    tvAuthor.text = it.cocktailAuthor
                    tvServing.text = it.cocktailServing
                }
            }
        }

        // Подписка на метод приготовления
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.makingMethodName.collect { methodName ->
                tvMethod.text = methodName
            }
        }

        // Подписка на список ингредиентов
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ingredients.collect { items ->
                ingredientAdapter.submitList(items)
            }
        }
        view.findViewById<FloatingActionButton>(R.id.fabCocktailDetail).setOnClickListener {
            val action = CocktailDetailFragmentDirections
                .actionCocktailDetailFragmentToCocktailEditFragment(-1L)
            findNavController().navigate(action)
        }
    }

}