package fr.uge.lootin.config

import android.content.Context
import android.preference.PreferenceManager

class Configuration {
    companion object {
        fun getUrl(context: Context): String {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val ip = prefs.getString("ip", "").toString()
            return "http://$ip:8080"
        }
    }
}
