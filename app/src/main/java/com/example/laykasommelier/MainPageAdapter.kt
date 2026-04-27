package com.example.laykasommelier

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPageAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CocktailFragment()
            1 -> DrinkFragment()
            2 -> AdminFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}