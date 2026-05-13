package com.example.laykasommelier

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laykasommelier.data.local.pojo.DrinkDetail
import com.example.laykasommelier.viewModels.DrinkDetailViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DrinkDetailFragment: Fragment() {

    private val viewModel: DrinkDetailViewModel by viewModels()
    lateinit var chosenDrink: DrinkDetail
    val args: DrinkDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.drink_detail_fragment,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ddIV: ImageView = view.findViewById(R.id.drinkDetailImage)
        val ddNameTV: TextView = view.findViewById(R.id.drinkDetailDrinkName)
        val ddTypeTV: TextView = view.findViewById(R.id.drinkDetailDrinkType)
        val ddSubTypeTV: TextView = view.findViewById(R.id.drinkDetailDrinkSubType)
        val ddCountryTV: TextView = view.findViewById(R.id.drinkDetailDrinkCountry)
        val ddProducerTV: TextView = view.findViewById(R.id.drinkDetailDrinkProducer)
        val ddAgedTV: TextView = view.findViewById(R.id.drinkDetailDrinkAged)
        val ddAbvTV: TextView = view.findViewById(R.id.drinkDetailDrinkAbv)
        val btnEdit: Button = view.findViewById(R.id.drinkEditBtn)

        btnEdit.setOnClickListener {
            val navController = NavHostFragment.findNavController(this@DrinkDetailFragment)
            val action = DrinkDetailFragmentDirections
                .actionDrinkDetailFragmentToDrinkEditFragment(args.drinkDetailID)
            navController.navigate(action)
        }
        val reviewRV: RecyclerView = view.findViewById(R.id.drinkReviewRV)
        reviewRV.layoutManager = LinearLayoutManager(requireContext())

        val reviewAdapter = DrinkReviewAdapter { url ->
            if (url != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
        reviewRV.adapter = reviewAdapter

        // Подписка на детали
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.drinkDetail.collect { drinkDetail ->
                drinkDetail?.let { drink ->
                    ddNameTV.text = drink.name
                    ddTypeTV.text = drink.type
                    ddSubTypeTV.text = drink.subType ?: ""
                    ddCountryTV.text = drink.country ?: ""
                    ddProducerTV.text = drink.producer ?: ""
                    ddAgedTV.text = if (drink.aged > 0) drink.aged.toString() else "–"
                    ddAbvTV.text = "${drink.abv}%"
                    // Картинка через Glide
                    //Glide.with(this@DrinkDetailFragment)
                    //    .load(drink.imageUrl)
                    //    .placeholder(R.drawable.ic_launcher_background)
                    //    .into(ddIV)
                }
            }
        }

        // Подписка на рецензии
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reviews.collect { reviews ->
                reviewAdapter.submitList(reviews)
            }
        }
        view.findViewById<FloatingActionButton>(R.id.fabDrinkDetail).setOnClickListener {
            val action = DrinkDetailFragmentDirections.actionDrinkDetailFragmentToDrinkEditFragment(-1L)
            findNavController().navigate(action)
        }
    }


}