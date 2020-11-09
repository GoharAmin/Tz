package com.gohar_amin.tz.utils

import android.content.Context
import android.content.SharedPreferences

class PrefHelper(var context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    fun saveString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    companion object {
        var _this: PrefHelper? = null
        fun getInstance(context: Context): PrefHelper? {
            if (_this == null) {
                _this = PrefHelper(context)
            }
            return _this
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
}