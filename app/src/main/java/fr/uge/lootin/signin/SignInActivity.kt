package fr.uge.lootin.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.config.ConfigurationDto
import fr.uge.lootin.register.RegisterActivity
import org.json.JSONObject
import java.io.IOException


class SignInActivity : AppCompatActivity() {
    private var username: String = ""
    private var password: String = ""
    private var baseUrl: String = ""

    private fun showToast(msg: Int) {
        Toast.makeText(
            applicationContext,
            applicationContext.getString(msg),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkParams(): Boolean {
        username = findViewById<EditText>(R.id.UsernameOnsignInPage).text.toString()
        password = findViewById<EditText>(R.id.PasswordOnsignInPage).text.toString()
        if (username == "" || password == "") {
            showToast(R.string.loginMessageError)
            return false
        }
        return true
    }

    private fun launchRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun saveJWT(jwt: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putString("jwt", jwt)
        editor.commit()
        val intent = Intent(this, ProfilesSwipingActivity::class.java)
        startActivity(intent)
    }

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

    private fun readConfigurationFile() {
        val jsonFileString = getJsonDataFromAsset(applicationContext, "config.json")
        val gson = Gson()
        val configType = object : TypeToken<ConfigurationDto>() {}.type
        var persons: ConfigurationDto = gson.fromJson(jsonFileString, configType)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putString("ip", persons.ip)
        editor.commit()
    }

    private fun login(queue: RequestQueue) {
        val url = "$baseUrl/login"
        Log.i("test", "login request")
        Log.i("test", "username=${this.username}, password=${this.password}")
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            JSONObject("{\"username\":\"${this.username}\",\"password\":\"${this.password}\"}"),
            Response.Listener { response -> saveJWT(response.getString("jwt")) },
            Response.ErrorListener { error ->
                Log.i(
                    "test", "error while trying to login\n"
                            + error.toString() + "\n"
                            + "code " + error.networkResponse.statusCode + "\n"
                            + "networkResponse " + error.networkResponse + "\n"
                            + "localizedMessage " + error.localizedMessage + "\n"
                            + "message " + error.message + "\n"
                            + "cause " + error.cause + "\n"
                            + "stackTrace " + error.stackTrace.toString()
                )
                if (error.networkResponse.statusCode == 403) {
                    showToast(R.string.incorrectLogin)
                }
            }) {
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/json"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readConfigurationFile()
        baseUrl = Configuration.getUrl(this)
        setContentView(R.layout.activity_sign_in)
        val queue = Volley.newRequestQueue(this)
        //connection button
        findViewById<Button>(R.id.ConnectionButtonOnsignInPage).setOnClickListener {
            if (checkParams()) login(
                queue
            )
        }

        //Register button
        findViewById<TextView>(R.id.RegisterLinkOnsignInPage).setOnClickListener { launchRegisterActivity() }
    }
}