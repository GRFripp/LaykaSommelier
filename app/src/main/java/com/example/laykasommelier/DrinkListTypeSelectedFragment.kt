package com.example.laykasommelier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrinkListTypeSelectedFragment: Fragment() {

    private val args: DrinkListTypeSelectedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drink_selected_type,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedType = args.selectedType
        view.findViewById<TextView>(R.id.drinkTypeSelectedtv).text = "Выбрана категория: $selectedType"
    }
}