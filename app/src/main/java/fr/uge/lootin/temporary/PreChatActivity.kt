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
import fr.uge.lootin.chat.ChatFragment
import fr.uge.lootin.chat.ChatFragment.Companion.MATCH_ID
import fr.uge.lootin.chat.ChatFragment.Companion.OTHER_NAME
import fr.uge.lootin.chat.ChatFragment.Companion.TOKEN_VALUE
import fr.uge.lootin.chat.ChatFragment.Companion.USER_ID
import fr.uge.lootin.chat.ChatFragment.Companion.TAG
import fr.uge.lootin.config.Configuration
import org.json.JSONObject

class PreChatActivity : AppCompatActivity() {

    var token : String = ""
    var match_id : Long = 0
    var localhost : String = "lootin.duckdns.org"
    val username : String = "tototaka"
    val password : String = "0000"
    val othername : String = "GTROS HIEN"
    var port = "1234"

    private fun connect(queue: RequestQueue){
        val url = "http://$localhost:$port/login"
        Log.i(TAG, "connect request hehe")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject("{\"username\": \"$username\",\"password\": \"$password\"}"),
            { response ->
                Log.i(TAG, "Connect Response: %s".format(response.toString()))
                val jsonResponse = JSONObject(response.toString())
                token = jsonResponse.getString("jwt")
                callChat()
            },
            { error ->
                Log.i(
                    TAG, "error while trying to connect\n"
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
        /*
        val intent = Intent(this, ChatFragment::class.java)
        xintent.putExtra(TOKEN_VALUE, token)
        intent.putExtra(MATCH_ID, match_id)
        intent.putExtra(USER_ID, user_id)
        intent.putExtra(USER_NAME, username)
        intent.putExtra(OTHER_NAME, othername)
        startActivityForResult(intent, 1)
        */

        val settingsFrag = ChatFragment.chatInstance( match_id, /*byteArrayOf(),*/ othername)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, settingsFrag, "settingsFragment")
                .addToBackStack("settingsFragment").commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_chat)

        port = Configuration.getPort(this)
        localhost = Configuration.getIp(this)
         val queue : RequestQueue = Volley.newRequestQueue(this)
        findViewById<Button>(R.id.buttonGo).setOnClickListener { connect(queue) }
    }
}