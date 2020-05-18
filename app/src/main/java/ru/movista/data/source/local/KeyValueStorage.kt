package ru.movista.data.source.local

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.movista.utils.EMPTY

class KeyValueStorage(private val sharedPrefs: SharedPreferences) {

    companion object {
        const val KEY_TOKEN = "TOKEN_V2"                // todo перетащить в конкретные репозитории
        const val KEY_IS_FIRST_RUN = "IS_FIRST_RUN"     // todo перетащить в конкретные репозитории
        const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"   // todo перетащить в конкретные репозитории
    }

    fun setAppFirstRun() {
        sharedPrefs.edit {
            putBoolean(KEY_IS_FIRST_RUN, false)
        }
    }

    fun removeAppFirstRun() {
        sharedPrefs.edit {
            remove(KEY_IS_FIRST_RUN)
        }
    }

    fun isAppFirstRun(): Boolean {
        return sharedPrefs.getBoolean(KEY_IS_FIRST_RUN, true)
    }

    fun saveToken(token: String) {
        sharedPrefs.edit {
            putString(KEY_TOKEN, token)
        }
    }

    fun getToken(): String {
        return sharedPrefs.getString(KEY_TOKEN, String.EMPTY) ?: String.EMPTY
    }

    fun clearToken() {
        sharedPrefs.edit {
            putString(KEY_TOKEN, String.EMPTY)
        }
    }

    fun saveRefreshToken(token: String) {
        return sharedPrefs.edit {
            putString(KEY_REFRESH_TOKEN, token)
        }
    }

    fun getRefreshToken(): String {
        return sharedPrefs.getString(KEY_REFRESH_TOKEN, String.EMPTY) ?: String.EMPTY
    }

    fun clearRefreshToken() {
        sharedPrefs.edit {
            putString(KEY_REFRESH_TOKEN, String.EMPTY)
        }
    }

    fun contains(key: String): Boolean {
        return sharedPrefs.contains(key)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPrefs.getString(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        sharedPrefs.edit {
            putString(key, value)
        }
    }

    fun putStrings(map: Map<String, String?>) {
        val editor = sharedPrefs.edit()
        map.forEach { editor.putString(it.key, it.value) }
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit {
            putBoolean(key, value)
        }
    }

    fun remove(key: String) {
        sharedPrefs.edit {
            remove(key)
        }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPrefs.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPrefs.edit { putInt(key, value) }
    }
}