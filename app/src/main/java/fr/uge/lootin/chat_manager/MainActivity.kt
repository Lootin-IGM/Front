package fr.uge.lootin.chat_manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.R
import org.json.JSONObject

const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
const val IP = "192.168.1.18"

class MainActivity : AppCompatActivity() {
    var token:String = "yesss"
    private fun connect(queue: RequestQueue){
        val url = URL + "/login"
        Log.i("my_log", "connect request")
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"username\": \"Loulou\",\"password\": \"Yvette\"}"),
                Response.Listener { response ->
                    Log.i("my_log", "Connect Response: %s".format(response.toString()));
                    val jsonResponse = JSONObject(response.toString());
                    this.token = jsonResponse.getString("jwt")

                },
                Response.ErrorListener { error ->
                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        Log.i("my_log", "Auth failed: wrong login and password combination")
                    }
                    else {
                        Log.i("my_log", "error while trying to connect\n"
                                + error.toString() + "\n"
                                + error.networkResponse + "\n"
                                + error.localizedMessage + "\n"
                                + error.message + "\n"
                                + error.cause + "\n"
                                + error.stackTrace.toString())
                    }
                }
        )
        queue.add(jsonObjectRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val queue = Volley.newRequestQueue(this)
        connect(queue)
        findViewById<Button>(R.id.main_button).setOnClickListener {

            val intent = Intent(this, ChatManagerActivity::class.java).apply {

                Log.i("my_log", "value: " + token)

                putExtra(TOKEN_VALUE, token)
            }

            startActivity(intent)
        }
    }
}