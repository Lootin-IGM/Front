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
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonParser
import fr.uge.lootin.R
import fr.uge.lootin.request.GsonGETRequest
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.nio.charset.Charset
import java.util.*
import java.io.UnsupportedEncodingException as UnsupportedEncodingException1

const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
const val MATCH_ID = "fr.uge.lootin.MATCHID"

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter
    private lateinit var webSocketClient: WebSocketClient
    lateinit var queue : RequestQueue
    lateinit var token : String
    var match_id : Long = 0
    val localhost : String = "192.168.1.58"
    var page : Int = 0

    override fun onResume() {
        super.onResume()
        initWebSocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    private fun initWebSocket() {
        val coinbaseUri: URI? = URI(WEB_SOCKET_URL)

        createWebSocketClient(coinbaseUri)
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                setUpBtcPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e("createWebSocketClient", "onError: ${ex?.message}")
            }
        }
    }

    /**
     * TODO
     */
    private fun subscribe() {
        webSocketClient.send(
                "{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                        "}"
        )
    }

    /**
     * TODO adapter
     */
    private fun setUpBtcPriceText(message: String?) {
        message?.let {
            /*
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<BitcoinTicker> = moshi.adapter(BitcoinTicker::class.java)
            val bitcoin = .fromJson(message)
            runOnUiThread { btc_price_tv.text = "1 BTC: ${bitcoin?.price} €" }
             */
        }
    }

    /**
     * TODO
     */
    private fun unsubscribe() {
        webSocketClient.send(
                "{\n" +
                        "    \"type\": \"unsubscribe\",\n" +
                        "    \"channels\": [\"ticker\"]\n" +
                        "}"
        )
    }


    private fun verifyConnect(queue: RequestQueue, token: String){
        val url = "http://$localhost:8080/showLogin"

        Log.i("my_log", "verify connexion request")
        val stringRequest = object : StringRequest(
                Request.Method.GET, url,
                Response.Listener { response ->
                    Log.i("my_log", "Response: %s".format(response))
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
        queue.add(stringRequest)
    }


    private fun receiveData(response: MessagesResponse){

        response.data.forEach{adapter.pushOldMessage(MessageItemUi(it.message, Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE, it.id, it.sendTime))}
    }

    private fun getMessages() {
        val url = "http://$localhost:8080/messages/$match_id/5/$page"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request =
            GsonGETRequest(url, MessagesResponse::class.java, map,
                { response ->
                    Log.i("my_log", response.toString())
                    receiveData(response)
                },
                { error -> Log.i("my_log", "error while trying to verify connexion\n"
                        + error.toString() + "\n"
                        + error.networkResponse + "\n"
                        + error.localizedMessage + "\n"
                        + error.message + "\n"
                        + error.cause + "\n"
                        + error.stackTrace.toString())
                }
            )
        queue.add(request)
    }


    private fun getOldMessages(nb_matches: Int, page: Int){

    }

    private fun getnewMessages(nb_matches: Int, page: Int){

    }

    /**
     * Send message
     */
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


    /**
     * Checker si on est bien connecté, sinon pop up + exit(0)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)
        queue = Volley.newRequestQueue(this)
        token = intent.getStringExtra(TOKEN_VALUE).toString()
        match_id = intent.getLongExtra(MATCH_ID, -1)
        Log.d("my_log", "token -> $token");
        Log.d("my_log", "match-id -> $match_id");

        //connect(queue)

        adapter = ChatAdapter(ArrayList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("stated", newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load messages*/
                    getMessages()
                    Log.d("my_log", "end");
                }
            }
        })

        //getMessages()
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener { sendText() }
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener { sendPicture() }
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener { sendVocal() }


    }



    private fun sendText() {
        //Toast.makeText(this, "Send message is not implemented yet", Toast.LENGTH_LONG).show()
        val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
        if (message.isNotEmpty()) {
            postMessages(message)
        }
    }

    private fun receiveText(message: String, id: Long, date: Date){
        adapter.pushFrontFirst(MessageItemUi(message, Color.WHITE, MessageItemUi.TYPE_MY_MESSAGE, id, date))
        findViewById<TextView>(R.id.zoneText).text = ""
        recycler.scrollToPosition(0)
    }

    private fun sendPicture() {
        Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
    }

    private fun sendVocal() {
        Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
    }



    companion object {
        const val WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com"
        const val TAG = "MESSAGE"
    }

}