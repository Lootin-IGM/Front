package fr.uge.lootin.config

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class Configuration {
    companion object {
        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun getUrl(context: Context): String {
            val jsonFileString = getJsonDataFromAsset(context, "config.json")
            val gson = Gson()
            val configType = object : TypeToken<ConfigurationDto>() {}.type
            var config: ConfigurationDto = gson.fromJson(jsonFileString, configType)
            val ip = config.ip
            return "http://$ip:8080"
        }

        fun getHostNameAndPort(context: Context): String?{
            val jsonFileString = getJsonDataFromAsset(context, "config.json")
            val gson = Gson()
            val configType = object : TypeToken<ConfigurationDto>() {}.type
            var config: ConfigurationDto = gson.fromJson(jsonFileString, configType)
            val ip = config.ip
            return "$ip:8080"
        }

        fun getIp(context: Context): String {
            val jsonFileString = getJsonDataFromAsset(context, "config.json")
            val gson = Gson()
            val configType = object : TypeToken<ConfigurationDto>() {}.type
            var config: ConfigurationDto = gson.fromJson(jsonFileString, configType)
            val ip = config.ip
            return "$ip"
        }

        fun getPort(context: Context): String {
            return "8080"
        }
    }
}
