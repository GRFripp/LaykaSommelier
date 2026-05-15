package com.example.laykasommelier

import android.content.Context
import android.content.SharedPreferences
import com.example.laykasommelier.data.local.pojo.EmployeeRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val authPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "firebase_id_token"
        private const val KEY_LOGGED_IN = "is_logged_in"
        private const val KEY_LOGIN_TIME = "login_timestamp"
        private const val MAX_SESSION_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 дней

        private const val KEY_ROLE = "role"
        private const val KEY_EMPLOYEE_ID = "employee_id"
    }

    fun saveSession(token: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putBoolean(KEY_LOGGED_IN, true)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            .apply()
    }

    fun saveRole(role: String) {
        authPrefs.edit().putString(KEY_ROLE, role).apply()
    }

    fun saveEmployeeId(id: Long) {
        authPrefs.edit().putLong(KEY_EMPLOYEE_ID, id).apply()
    }

    fun getRole(): String = authPrefs.getString(KEY_ROLE, EmployeeRole.ASSISTANT) ?: EmployeeRole.ASSISTANT
    fun getEmployeeId(): Long = authPrefs.getLong(KEY_EMPLOYEE_ID, -1L)

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false)
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        return loggedIn && (currentTime - loginTime) < MAX_SESSION_DURATION
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        authPrefs.edit().clear().apply()
    }
}