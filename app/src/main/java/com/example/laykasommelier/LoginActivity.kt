package com.example.laykasommelier

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.TokenRequest
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Запускаем корутину для входа
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // 1. Вход через Firebase
                    val authResult = FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .await()

                    // 2. Получаем ID-токен
                    val idToken = authResult.user?.getIdToken(false)?.await()?.token
                        ?: throw Exception("Не удалось получить токен")

                    // 3. Проверяем токен на сервере
                    val response = apiService.verifyToken(TokenRequest(idToken))

                    getSharedPreferences("app_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("firebase_id_token", idToken)
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Успех! UID: ${response.uid}",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}