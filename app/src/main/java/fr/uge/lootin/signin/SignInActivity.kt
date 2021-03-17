package fr.uge.lootin.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.register.RegisterActivity
import org.json.JSONObject


class SignInActivity : AppCompatActivity() {
    private var username: String = ""
    private var password: String = ""

    private fun checkParams(): Boolean {
        username = findViewById<EditText>(R.id.UsernameOnsignInPage).text.toString()
        password = findViewById<EditText>(R.id.PasswordOnsignInPage).text.toString()
        if (username == "" || password == "") {
            Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.loginMessageError),
                    Toast.LENGTH_SHORT
            ).show()
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

    private fun login(queue: RequestQueue) {
        val url = "http://192.168.1.86:8080/login"
        Log.i("test", "login request")
        Log.i("test", "username=${this.username}, password=${this.password}")
        val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, JSONObject("{\"username\":\"${this.username}\",\"password\":\"${this.password}\"}"),
                Response.Listener { response -> saveJWT(response.getString("jwt")) },
                Response.ErrorListener { error ->
                    Log.i("test", "error while trying to login\n"
                            + error.toString() + "\n"
                            + "networkResponse " + error.networkResponse + "\n"
                            + "localizedMessage " + error.localizedMessage + "\n"
                            + "message " + error.message + "\n"
                            + "cause " + error.cause + "\n"
                            + "stackTrace " + error.stackTrace.toString())
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
        setContentView(R.layout.activity_sign_in)
        val queue = Volley.newRequestQueue(this)
        //connection button
        findViewById<Button>(R.id.ConnectionButtonOnsignInPage).setOnClickListener { if (checkParams()) login(queue) }

        //Register button
        findViewById<TextView>(R.id.RegisterLinkOnsignInPage).setOnClickListener { launchRegisterActivity() }
    }
}