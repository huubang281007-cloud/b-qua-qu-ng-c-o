package com.example.skipad

import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> refresh() }

    private lateinit var heroCard: android.view.View
    private lateinit var statusTitle: TextView
    private lateinit var statusSub: TextView
    private lateinit var statsCount: TextView
    private lateinit var statsLast: TextView
    private lateinit var openSettings: MaterialButton
    private lateinit var swEnabled: MaterialSwitch
    private lateinit var swBrowsers: MaterialSwitch
    private lateinit var swToast: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs.get(this)

        heroCard = findViewById(R.id.heroCard)
        statusTitle = findViewById(R.id.statusTitle)
        statusSub = findViewById(R.id.statusSub)
        statsCount = findViewById(R.id.statsCount)
        statsLast = findViewById(R.id.statsLast)
        openSettings = findViewById(R.id.openSettings)
        swEnabled = findViewById(R.id.swEnabled)
        swBrowsers = findViewById(R.id.swBrowsers)
        swToast = findViewById(R.id.swToast)

        openSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<MaterialButton>(R.id.resetStats).setOnClickListener {
            prefs.edit().putInt(Prefs.KEY_COUNT, 0).remove(Prefs.KEY_LAST).apply()
        }

        swEnabled.isChecked = prefs.getBoolean(Prefs.KEY_ENABLED, true)
        swBrowsers.isChecked = prefs.getBoolean(Prefs.KEY_BROWSERS, true)
        swToast.isChecked = prefs.getBoolean(Prefs.KEY_TOAST, false)

        swEnabled.setOnCheckedChangeListener { _, c -> prefs.edit().putBoolean(Prefs.KEY_ENABLED, c).apply() }
        swBrowsers.setOnCheckedChangeListener { _, c -> prefs.edit().putBoolean(Prefs.KEY_BROWSERS, c).apply() }
        swToast.setOnCheckedChangeListener { _, c -> prefs.edit().putBoolean(Prefs.KEY_TOAST, c).apply() }
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(listener)
        refresh()
    }

    override fun onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onPause()
    }

    private fun refresh() {
        val serviceOn = isServiceEnabled()
        val active = serviceOn && prefs.getBoolean(Prefs.KEY_ENABLED, true)

        heroCard.setBackgroundResource(if (active) R.drawable.bg_hero_on else R.drawable.bg_hero_off)
        statusTitle.setText(if (serviceOn) R.string.status_on_title else R.string.status_off_title)
        statusSub.setText(if (serviceOn) R.string.status_on_sub else R.string.status_off_sub)
        openSettings.visibility = if (serviceOn) android.view.View.GONE else android.view.View.VISIBLE

        val count = prefs.getInt(Prefs.KEY_COUNT, 0)
        statsCount.text = getString(R.string.stats_count, count)
        val last = prefs.getLong(Prefs.KEY_LAST, 0L)
        statsLast.text = if (count > 0 && last > 0) {
            getString(
                R.string.stats_last,
                DateUtils.getRelativeTimeSpanString(last, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
            )
        } else getString(R.string.stats_never)
    }

    private fun isServiceEnabled(): Boolean {
        val expected = ComponentName(this, SkipAdService::class.java)
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?: return false
        val splitter = TextUtils.SimpleStringSplitter(':').apply { setString(enabled) }
        while (splitter.hasNext()) {
            if (ComponentName.unflattenFromString(splitter.next()) == expected) return true
        }
        return false
    }
}
