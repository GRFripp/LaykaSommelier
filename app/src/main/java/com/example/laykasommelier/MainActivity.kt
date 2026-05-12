package com.example.laykasommelier

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.laykasommelier.data.local.database.AppDatabase
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.entities.Review
import com.example.laykasommelier.data.local.entities.Source
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Белый статус-бар и тёмные иконки
        window.statusBarColor = android.graphics.Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        window.navigationBarColor = android.graphics.Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Нижний бар и ViewPager2
        val bottomBar = findViewById<LinearLayout>(R.id.bottomNavBar)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)

        // Обработка системных отступов (status bar и navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<FrameLayout>(R.id.mainContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Поднимаем нижний бар над системной навигационной панелью
            bottomBar.updateLayoutParams<FrameLayout.LayoutParams> {
                bottomMargin = systemBars.bottom
            }

            // Увеличиваем нижний отступ ViewPager2, чтобы последний элемент списка был виден
            viewPager.setPadding(
                viewPager.paddingLeft,
                viewPager.paddingTop,
                viewPager.paddingRight,
                systemBars.bottom + bottomBar.height
            )

            insets
        }

        // Адаптер страниц (Коктейли, Напитки, Админ)
        viewPager.adapter = MainPageAdapter(this)

        // Кнопки нижнего меню
        findViewById<Button>(R.id.btnCocktails).setOnClickListener {
            viewPager.setCurrentItem(0, true)
        }
        findViewById<Button>(R.id.btnDrinks).setOnClickListener {
            viewPager.setCurrentItem(1, true)
        }
        findViewById<Button>(R.id.btnAdmin).setOnClickListener {
            viewPager.setCurrentItem(2, true)
        }

        // Логотип (скролл на первую страницу)
        findViewById<ImageView>(R.id.logoButton).setOnClickListener {
            viewPager.setCurrentItem(0, true)
        }

        // Кнопка конверта
        findViewById<ImageButton>(R.id.envelopeButton).setOnClickListener {
            Toast.makeText(this, "Раздел в разработке", Toast.LENGTH_SHORT).show()
        }


    }
}