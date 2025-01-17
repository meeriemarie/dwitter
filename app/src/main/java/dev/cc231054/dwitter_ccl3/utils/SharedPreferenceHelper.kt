package dev.cc231054.dwitter_ccl3.utils

import android.content.Context

class SharedPreferenceHelper(private val context: Context) {
    companion object{
        private const val MY_PFREF_KEY = "MY_PREF"
    }

    fun saveStringData(key: String, data: String) {
        val sharedPreferences = context.getSharedPreferences(MY_PFREF_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key,data).apply()
    }

    fun getStringData(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(MY_PFREF_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}