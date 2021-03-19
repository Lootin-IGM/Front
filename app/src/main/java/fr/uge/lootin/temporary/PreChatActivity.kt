package fr.uge.lootin.temporary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.R
import fr.uge.lootin.chat.ChatActivity
import fr.uge.lootin.chat.ChatActivity.Companion.MATCH_ID
import fr.uge.lootin.chat.ChatActivity.Companion.TOKEN_VALUE
import fr.uge.lootin.chat.ChatActivity.Companion.USER_ID
import org.json.JSONObject

class PreChatActivity : AppCompatActivity() {

    var token : String = ""
    var match_id : Long = 0
    val user_id : Long = 1
    val localhost : String = "192.168.1.58"
    val username : String = "Loulou"
    val password : String = "Yvette"

    private fun connect(queue: RequestQueue){
        val url = "http://$localhost:8080/login"
        Log.i("my_loge", "connect request hehe")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject("{\"username\": \"$username\",\"password\": \"$password\"}"),
            { response ->
                Log.i("my_log", "Connect Response: %s".format(response.toString()))
                val jsonResponse = JSONObject(response.toString())
                this.token = jsonResponse.getString("jwt")
                callChat()
            },
            { error ->
                Log.i(
                    "my_log", "error while trying to connect\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString()
                )
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun callChat(){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(TOKEN_VALUE, token)
        intent.putExtra(MATCH_ID, match_id)
        intent.putExtra(USER_ID, user_id)
        startActivityForResult(intent, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_chat)

        val queue : RequestQueue = Volley.newRequestQueue(this)
        findViewById<Button>(R.id.buttonGo).setOnClickListener { connect(queue) }
    }
}