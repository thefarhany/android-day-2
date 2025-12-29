package com.thefarhany.tugas2.data.local

import android.content.Context
import android.content.SharedPreferences
import com.thefarhany.tugas2.model.User
import com.thefarhany.tugas2.utils.Constants.KEY_EMAIL
import com.thefarhany.tugas2.utils.Constants.KEY_IS_LOGGED_IN
import com.thefarhany.tugas2.utils.Constants.KEY_NAME
import com.thefarhany.tugas2.utils.Constants.KEY_PASSWORD
import com.thefarhany.tugas2.utils.Constants.PREF_NAME

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PASSWORD, user.password)
            .apply()
    }

    fun getUser(): User? {
        val name = prefs.getString(KEY_NAME, null)
        val email = prefs.getString(KEY_EMAIL, null)
        val password = prefs.getString(KEY_PASSWORD, null)

        return if (name != null && email != null && password != null) {
            User(name, email, password)
        } else {
            null
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}