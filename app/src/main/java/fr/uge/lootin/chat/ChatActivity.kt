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

const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
const val MATCH_ID = "fr.uge.lootin.MATCHID"

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter
    var match_id : Long = 0
    var page : Int = 0

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }






    private fun getOldMessages(nb_matches: Int, page: Int){
        /*TODO*/
    }


    private fun getnewMessages(nb_matches: Int, page: Int){
        /*TODO*/
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



    /**
     * Checker si on est bien connecté, sinon pop up + exit(0)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)
        val token = intent.getStringExtra(TOKEN_VALUE).toString()
        match_id = intent.getLongExtra(MATCH_ID, -1)



        //connect(queue)

        adapter = ChatAdapter(ArrayList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        val restService = RestService(LOCALHOST, MATCH_ID, PAGE_SIZE, adapter )

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("stated", newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load messages*/
                    restService.getMessages()
                    Log.d("my_log", "end");
                }
            }
        })

        //getMessages()
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener { sendText() }
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener { sendPicture() }
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener { sendVocal() }


    }

    /**
     * Send text message (call web socket
     */
    private fun sendText() {
        val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
        if (message.isNotEmpty()) {
            //postMessages(message)
        }
    }

    //TODO virer moi ça ...
    private fun receiveText(message: String, id: Long, date: Date){
        adapter.pushFrontFirst(MessageItemUi(message, Color.WHITE, MessageItemUi.TYPE_MY_MESSAGE, id, date))
        findViewById<TextView>(R.id.zoneText).text = ""
        recycler.scrollToPosition(0) //TODO ça c'est cool
    }

    // TODO faire avec web socket
    private fun sendPicture() {
        Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
    }

    // HAha on verra ça les bg
    private fun sendVocal() {
        Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "MAINACTIVITY"
        const val LOGIN = "login"
        const val PASSCODE = "passcode"

        const val LOCALHOST: String = "192.168.1.58"
        const val PORT:String = "8080"
        const val ENPOINT: String = "gs-guide-websocket"
        const val PAGE_SIZE: String = "10S"
    }

}