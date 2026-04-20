package com.example.laykasommelier

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.laykasommelier.data.local.database.AppDatabase
import com.example.laykasommelier.data.local.entities.Drink
import com.example.laykasommelier.data.local.entities.Review
import com.example.laykasommelier.data.local.entities.Source
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            // --- ВСТАВКА ТЕСТОВЫХ ДАННЫХ ---
            val source1 = Source(sourceName = "Официальный сайт", sourceUrl = "https://example.com")
            val sourceId = db.sourceDao().insertSource(source1)

            val drink1 = Drink(
                drinkName = "Виски односолодовый",
                drinkType = "Виски",
                drinkSubType = "Односолодовый",
                drinkCountry = "Шотландия",
                drinkProducer = "Glenfiddich",
                drinkAged = 12,
                drinkAbv = 40.0,

            )
            val drinkId = db.drinkDao().insertDrink(drink1)

            val review1 = Review(
                reviewedDrinkID = drinkId,
                reviewSourceID = sourceId,
                reviewUrl = "https://example.com/review/123"
            )
            db.reviewDao().insertReview(review1)

            // --- ЧТЕНИЕ И ВЫВОД В LOG ---
            val drinks = db.drinkDao().getAllDrinks().first()
            Log.d("DB_TEST", "Напитков в базе: ${drinks.size}")
            drinks.forEach { drink ->
                Log.d("DB_TEST", "Напиток: ${drink.drinkName}, ${drink.drinkType}, ${drink.drinkProducer}")
            }

            val reviews = db.reviewDao().getAllReviews().first()
            Log.d("DB_TEST", "Рецензий в базе: ${reviews.size}")
        }
    }
}