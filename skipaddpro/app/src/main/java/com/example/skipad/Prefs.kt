package com.example.skipad

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    const val NAME = "skipad"
    const val KEY_ENABLED = "enabled"
    const val KEY_BROWSERS = "browsers"
    const val KEY_TOAST = "toast"
    const val KEY_COUNT = "count"
    const val KEY_LAST = "last"

    fun get(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun addSkipped(ctx: Context) {
        val p = get(ctx)
        p.edit()
            .putInt(KEY_COUNT, p.getInt(KEY_COUNT, 0) + 1)
            .putLong(KEY_LAST, System.currentTimeMillis())
            .apply()
    }
}
