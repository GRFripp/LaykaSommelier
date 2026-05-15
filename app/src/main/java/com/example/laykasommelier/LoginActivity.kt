package com.example.laykasommelier

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.laykasommelier.network.ApiService
import com.example.laykasommelier.network.TokenRequest
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.example.laykasommelier.network.dto.*
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Если сессия ещё жива – сразу на главный экран
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

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

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // 1. Вход через Firebase
                    val authResult = FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .await()
                    FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("FCM", "Subscribed to all_users topic")
                            } else {
                                Log.e("FCM", "Subscription failed", task.exception)
                            }
                        }
                    // 2. Получаем ID-токен
                    val idToken = authResult.user?.getIdToken(false)?.await()?.token
                        ?: throw Exception("Не удалось получить токен")

                    // 3. Проверяем токен на сервере
                    val response = apiService.verifyToken(TokenRequest(idToken))

                    sessionManager.saveSession(idToken)
                    sessionManager.saveRole(response.role)
                    sessionManager.saveEmployeeId(response.employeeId)

                    Toast.makeText(
                        this@LoginActivity,
                        "Успех! UID: ${response.uid}",
                        Toast.LENGTH_LONG
                    ).show()

                    // 5. Переход на главный экран
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    Log.d("Session", "Role: ${sessionManager.getRole()}, EmployeeId: ${sessionManager.getEmployeeId()}")
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