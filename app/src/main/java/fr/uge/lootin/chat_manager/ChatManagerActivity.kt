package fr.uge.lootin.chat_manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.*
import fr.uge.lootin.R
import fr.uge.lootin.chat_manager.match.Match
import fr.uge.lootin.chat_manager.match.MatchAdapter
import fr.uge.lootin.chat_manager.preview_message.PreviewMessage
import fr.uge.lootin.chat_manager.preview_message.PreviewMessageAdapter
import org.json.JSONObject

const val URL = "http://192.168.56.1:8080"


const val SIZE_PAGE_MATCHES = 6
const val SIZE_PAGE_PREVIEW_MESSAGE = 7

class ChatManagerActivity : AppCompatActivity() {

    private fun fromStringToBitmap(image: String) : Bitmap {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun requestVerifyConnect(queue: RequestQueue, token: String){
        val url = URL + "/showLogin"

        Log.i("my_log", "verify connexion request")
        val stringRequest = object : StringRequest(Request.Method.GET, url,
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
    private fun treatEmptyMatches(response: JSONObject, list_matches: ArrayList<Match>, matchAdapter: MatchAdapter, page: Int) {
        val data = response.getJSONArray("data")
        val matchesSize = list_matches.size
        val pos = page * SIZE_PAGE_MATCHES
        val from = list_matches.size - pos
        if (from >= data.length()) {
            return
        }
        for (i in from until data.length()) {
            val match = data.getJSONObject(i)
            if (match.isNull("lastMessage")) {
                list_matches.add(Match(Integer.valueOf(match.getString("id")), match.getJSONObject("user").getString("login"), Integer.valueOf(match.getJSONObject("user").getString("id")), fromStringToBitmap(match.getJSONObject("user").getString("image"))))
            }
        }
        matchAdapter.notifyItemInserted(matchesSize)
    }

    private fun treatLastMessage(response: JSONObject, list_messages: ArrayList<PreviewMessage>, previewMessageAdapter: PreviewMessageAdapter, page: Int) {
        val data = response.getJSONArray("data")
        val previewMessagesSize = list_messages.size
        val pos = page * SIZE_PAGE_PREVIEW_MESSAGE
        val from = list_messages.size - pos
        if (from >= data.length()) {
            return
        }
        for (i in from until data.length()) {
            val match = data.getJSONObject(i)

            val lastMessage = match.getJSONObject("lastMessage")
            //list_messages.add(PreviewMessage(Integer.valueOf(match.getString("id")), lastMessage.getString("message"), match.getJSONObject("user").getString("login"), Integer.valueOf(match.getJSONObject("user").getString("id")), fromStringToBitmap(match.getJSONObject("user").getString("image")), lastMessage.getString("sendTime")))

        }
        previewMessageAdapter.notifyItemInserted(previewMessagesSize)
    }

    private fun requestGetEmptyMatches(queue: RequestQueue, token: String, nb_matches: Int, list_matches: ArrayList<Match>, matchAdapter: MatchAdapter) {
        val url = URL + "/matches/empty"
        var page = list_matches.size / SIZE_PAGE_MATCHES
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                object : Response.Listener<JSONObject?>{
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            treatEmptyMatches(response, list_matches, matchAdapter, page)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        Log.i("my_log", "Invalid token")
                    }
                    else {
                        Log.i("my_log", "error while trying to get matches\n"
                                + error.toString() + "\n"
                                + "networkResponse " + error.networkResponse + "\n"
                                + "localizedMessage " + error.localizedMessage + "\n"
                                + "message " + error.message + "\n"
                                + "cause " + error.cause + "\n"
                                + "stackTrace " + error.stackTrace.toString())
                    }
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

    private fun requestGetLastMessages(queue: RequestQueue, token: String, nb_matches: Int, list_messages: ArrayList<PreviewMessage>, previewMessageAdapter: PreviewMessageAdapter) {
        val url = URL + "/matches/lastMsg"
        var page = list_messages.size / SIZE_PAGE_PREVIEW_MESSAGE
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                object : Response.Listener<JSONObject?>{
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            treatLastMessage(response, list_messages, previewMessageAdapter, page)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    if (error.toString().equals("com.android.volley.AuthFailureError")) {
                        Log.i("my_log", "Invalid token")
                    }
                    else {
                        Log.i("my_log", "error while trying to get matches\n"
                                + error.toString() + "\n"
                                + "networkResponse " + error.networkResponse + "\n"
                                + "localizedMessage " + error.localizedMessage + "\n"
                                + "message " + error.message + "\n"
                                + "cause " + error.cause + "\n"
                                + "stackTrace " + error.stackTrace.toString())
                    }
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
        setContentView(R.layout.activity_chat_manager)

        val queue = Volley.newRequestQueue(this)
        val tmp_token: String? = intent.getStringExtra(TOKEN_VALUE)
        var token = ""
        if(tmp_token != null) {
            token = tmp_token
        }

        val listMessages = ArrayList<PreviewMessage>()
        val previewMessagesAdapter = PreviewMessageAdapter(listMessages, this)
        var recyclerViewMessagePreview : RecyclerView = findViewById(R.id.previewMessagesId)
        recyclerViewMessagePreview.adapter = previewMessagesAdapter
        recyclerViewMessagePreview.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        recyclerViewMessagePreview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, listMessages, previewMessagesAdapter)
                }
            }
        })

        val listMatches = ArrayList<Match>()
        val matchesAdapter = MatchAdapter(listMatches, this)
        var recyclerViewMatches : RecyclerView = findViewById(R.id.matchRecyclerView)
        recyclerViewMatches.adapter = matchesAdapter
        recyclerViewMatches.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        recyclerViewMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, listMatches, matchesAdapter)
                }
            }
        })

        requestVerifyConnect(queue, token)
        requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, listMatches, matchesAdapter)
        requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, listMessages, previewMessagesAdapter)

        /*
        val layout : ExpandableLayout = findViewById(R.id.expandable_layout)
        val renderer = MyRenderer()
        layout.setRenderer(renderer)*/

        /*
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Log.i("my_log", "deprecated method: " + height + "     " + width)


        var chatManager : ConstraintLayout =findViewById(R.id.chat_manager_id)

        Log.i("my_log", "chatManager.height and width: " + chatManager.height + "     " + chatManager.width)
        Log.i("my_log", "chatManager.measuredHeight and measuredWidth: " + chatManager.measuredWidth + "     " + chatManager.measuredHeight)
        Log.i("my_log", "chatManager.bacckgroundColor " + chatManager.background)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width2: Int = size.x
        val height2: Int = size.y
        Log.i("my_log", "deprecated method 2 : " + height2 + "     " + width2)
        Log.i("my_log", "deprecated method 2 : " + height2 + "     " + width2)
        Log.i("my_log", "deprecated method 2 : " + height2 + "     " + width2)*/


    }

    

}