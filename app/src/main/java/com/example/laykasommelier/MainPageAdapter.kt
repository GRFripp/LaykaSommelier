package com.example.laykasommelier

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.laykasommelier.data.local.pojo.EmployeeRole

class MainPageAdapter(
    activity: FragmentActivity,
    private val role: String
) : FragmentStateAdapter(activity) {

    // Количество страниц: Коктейли + Напитки + (для менеджера Админ) + Предложения
    override fun getItemCount(): Int = if (role == EmployeeRole.MANAGER) 4 else 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SuggestionFragment()
            1 -> CocktailFragment()
            2 -> DrinkFragment()
            3 -> {
                if (role == EmployeeRole.MANAGER) {
                    AdminFragment()
                } else {
                    SuggestionFragment()
                }
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}