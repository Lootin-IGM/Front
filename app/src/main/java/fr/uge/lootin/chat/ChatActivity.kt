package fr.uge.lootin.chat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.chat.services.RestService
import fr.uge.lootin.dto.MessagesResponse
import fr.uge.lootin.request.GsonGETRequest
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.*



class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }


    /**
     * Checker si on est bien connecté, sinon pop up + exit(0)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)
        val token = intent.getStringExtra(TOKEN_VALUE).toString()
        val matchId = intent.getLongExtra(MATCH_ID, -1)

        adapter = ChatAdapter(ArrayList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        val restService = RestService(LOCALHOST, matchId, PAGE_SIZE, adapter, token)

        // Create scrollListener
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i(TAG, newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                //TODO heheh ----------restService.getMessages()
                }
            }
        })

        //TODO call getMessages() and look pages

        // Send Text messages
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener {
            val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
            if (message.isNotEmpty()) {
                // TODO postMessages(message)
            }
        }

        //TODO Send Picture messages (WS)
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
        }

        //TODO Send Vocal messages (WS)
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener {
            Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
        }
    }


    //TODO virer moi ça ...
    private fun receiveText(message: String, id: Long, date: Date){

        adapter.pushFrontFirst(MessageItemUi.factoryMessageItemUI(message, id, date, true))
        findViewById<TextView>(R.id.zoneText).text = ""
        recycler.scrollToPosition(0) //TODO ça c'est cool
    }


    companion object {
        private const val TAG = "MAINACTIVITY"
        const val LOGIN = "login"
        const val PASSCODE = "passcode"

        const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
        const val MATCH_ID = "fr.uge.lootin.MATCHID"

        const val LOCALHOST: String = "192.168.1.58"
        const val PORT:String = "8080"
        const val ENPOINT: String = "gs-guide-websocket"

        const val PAGE_SIZE: Long = 10
    }

}


/**
 * Send message

private fun postMessages(content: String) {
val url = "http://$localhost:8080/msg/newMessage"

val jsonObjectRequest = object : JsonObjectRequest(
Request.Method.POST, url, JSONObject("{\"text\": $content,\"matchId\":$match_id}"),
Response.Listener { response ->
receiveText(response["message"] as String, response["id"] as Long, response["Timestamp"] as Date)
Log.i("my_log", "Response: %s".format(response.toString()))
},
Response.ErrorListener { error ->
Log.i("my_log", "error while trying to verify connexion\n"
+ error.toString() + "\n"
+ error.networkResponse + "\n"
+ error.localizedMessage + "\n"
+ error.message + "\n"
+ error.cause + "\n"
+ error.stackTrace.toString())
}
) {
@Throws(AuthFailureError::class)
override fun getHeaders(): Map<String, String>? {
val params: MutableMap<String, String> = HashMap()
params["Authorization"] = "Bearer $token"

return params
}
}
queue.add(jsonObjectRequest)
}
 */
