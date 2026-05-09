package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkDetailFragment: Fragment() {

    private val viewModel: DrinkDetailViewModel by viewModels()
    lateinit var chosenDrink: DrinkDetail

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drink_detail_fragment,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ddIV : ImageView = view.findViewById(R.id.drinkDetailImage)
        val ddNameTV : TextView = view.findViewById(R.id.drinkDetailDrinkName)
        val ddTypeTV : TextView = view.findViewById(R.id.drinkDetailDrinkType)
        val ddSubTypeTV : TextView = view.findViewById(R.id.drinkDetailDrinkSubType)
        val ddCountryTV : TextView = view.findViewById(R.id.drinkDetailDrinkCountry)
        val ddProducerTV : TextView = view.findViewById(R.id.drinkDetailDrinkProducer)
        val ddAgedTV : TextView = view.findViewById(R.id.drinkDetailDrinkAged)
        val ddAbvTV : TextView = view.findViewById(R.id.drinkDetailDrinkAbv)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.drink.collect { drinkDetail ->
                drinkDetail.let{ drink ->
                    ddNameTV.text = drink.name


            }
            }
        }
    }


}