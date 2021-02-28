package fr.uge.lootin.chat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import fr.uge.lootin.R
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.*
import kotlin.jvm.Throws

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter
    private lateinit var webSocketClient: WebSocketClient

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
        val url = "http://192.168.43.2:8080/showLogin"

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
                params["Authorization"] = "Bearer " + token
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun getMatches(queue: RequestQueue, token: String, nb_matches: Int, page: Int) {
        val url = "http://192.168.43.2:8080/matches"

        Log.i("my_log", "get matches request")
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page +"}"),
            Response.Listener { response ->
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
                params["Authorization"] = "Bearer " + token

                return params
            }
        }
        queue.add(jsonObjectRequest)
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)

        
        val tmpMessage = listOf<MessageItemUi>( MessageItemUi("j'adore manger", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE), MessageItemUi("yeeees", Color.DKGRAY, MessageItemUi.TYPE_MY_MESSAGE), MessageItemUi("go domac a 19h", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE) ,  MessageItemUi("Hey", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE),  MessageItemUi("Yo, on se capte ce soir ?", Color.DKGRAY, MessageItemUi.TYPE_MY_MESSAGE))
        val mutableList : MutableList<MessageItemUi> = ArrayList()
        for(e in tmpMessage) {
            mutableList.add(e)
        }
        adapter = ChatAdapter(mutableList)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("stated", newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load messages*/
                    Log.d("-----", "end");
                }
            }
        })


        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener { sendText() }
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener { SendPicture() }
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener { SendVocal() }


    }



    fun sendText() {
        //Toast.makeText(this, "Send message is not implemented yet", Toast.LENGTH_LONG).show()
        val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
        if (message.isNotEmpty()) {
            adapter.pushFrontFirst(MessageItemUi(message,  Color.WHITE, MessageItemUi.TYPE_MY_MESSAGE))
            findViewById<TextView>(R.id.zoneText).text = ""
            recycler.scrollToPosition(0)
        }
    }

    fun SendPicture() {
        Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
    }

    fun SendVocal() {
        Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
    }



    companion object {
        const val WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com"
        const val TAG = "Coinbase"
    }

}