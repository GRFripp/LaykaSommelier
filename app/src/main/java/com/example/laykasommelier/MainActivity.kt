package com.example.laykasommelier

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.laykasommelier.data.local.pojo.EmployeeRole
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.firebase.messaging.FirebaseMessaging
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Настройка статус-бара и панели навигации
        window.statusBarColor = android.graphics.Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        window.navigationBarColor = android.graphics.Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val bottomBar = findViewById<LinearLayout>(R.id.bottomNavBar)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)

        // Обработка системных отступов (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            bottomBar.setPadding(0, 0, 0, systemBars.bottom)
            viewPager.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val role = sessionManager.getRole()
        val isManager = role == EmployeeRole.MANAGER

        // Адаптер ViewPager: 0 - заявки, 1 - коктейли, 2 - напитки, 3 - админ (только менеджер)
        viewPager.adapter = MainPageAdapter(this, role)

        // Индексы страниц
        val suggestionPageIndex = 0
        val cocktailPageIndex = 1
        val drinkPageIndex = 2
        val adminPageIndex = 3

        // Кнопка "Админ" видна только менеджеру
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        btnAdmin.visibility = if (isManager) View.VISIBLE else View.GONE

        // Кнопки нижнего меню
        findViewById<Button>(R.id.btnCocktails).setOnClickListener {
            viewPager.setCurrentItem(cocktailPageIndex, false)
        }
        findViewById<Button>(R.id.btnDrinks).setOnClickListener {
            viewPager.setCurrentItem(drinkPageIndex, false)
        }
        btnAdmin.setOnClickListener {
            if (isManager) viewPager.setCurrentItem(adminPageIndex, false)
        }

        // Логотип – возврат на коктейли
        findViewById<ImageView>(R.id.logoButton).setOnClickListener {
            viewPager.setCurrentItem(cocktailPageIndex, false)
        }

        // Кнопка конверта – переход на заявки
        findViewById<ImageButton>(R.id.envelopeButton).setOnClickListener {
            viewPager.setCurrentItem(suggestionPageIndex, false)
        }

        // Выход из аккаунта
        findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            performLogout()
        }

        // Обработка системной кнопки "Назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.currentItem == suggestionPageIndex) {
                    viewPager.setCurrentItem(cocktailPageIndex, false)
                } else {
                    finish()
                }
            }
        })

        // === ОБРАБОТКА ПЕРЕХОДА ПО УВЕДОМЛЕНИЮ ===
        if (intent?.getBooleanExtra("open_suggestions", false) == true) {
            viewPager.setCurrentItem(suggestionPageIndex, false)
            intent?.removeExtra("open_suggestions")
        }
    }

    private fun performLogout() {
        // Отписка от топика уведомлений
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Unsubscribed from all_users topic")
                } else {
                    Log.e("FCM", "Unsubscribe failed", task.exception)
                }
            }

        sessionManager.clearSession()
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}