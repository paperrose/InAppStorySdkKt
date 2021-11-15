package com.inappstory.sdk.utils.localpreferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PreferencesDataStore(context: Context) {

    var context: Context? = context

    private val defaultPrefs = "default_n"

    @DelicateCoroutinesApi
    fun saveString(key: String, value: String?) {
        GlobalScope.launch {
            context?.let {
                val editor =
                    getPrefs(it).edit()
                editor.putString(key, value)
                editor.apply()
            }
        }
    }

    fun saveStringSet(key: String, value: String?) {
        context?.let {
            val editor =
                getPrefs(it).edit()
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun getString(key: String, defValue: String? = null): String? {
        context?.let {
            return getPrefs(it).getString(key, defValue)
        }
        return defValue
    }

    fun getStringSet(key: String): Set<String>? {
        context?.let {
            return getPrefs(it).getStringSet(key, null)
        }
        return null
    }

    fun remove(key: String) {
        context?.let {
            val editor =
                getPrefs(it).edit()
            editor.remove(key)
            editor.apply()
        }
    }

    fun remove(keys: Set<String>) {
        context?.let {
            val editor =
                getPrefs(it).edit()
            keys.forEach { key ->
                editor.remove(key)
            }
            editor.apply()
        }
    }


    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(defaultPrefs, Context.MODE_PRIVATE)
    }

}