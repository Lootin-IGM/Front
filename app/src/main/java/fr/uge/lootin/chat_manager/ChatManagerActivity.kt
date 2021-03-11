package fr.uge.lootin.chat_manager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.*
import fr.uge.lootin.R
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.math.sign

const val URL = "http://192.168.43.2:8080"

// TODO ATTENTION, NE DOIVENT PAS ETRE NULS!!!!!!!!
const val SIZE_PAGE_MATCHES = 1
const val SIZE_PAGE_PREVIEW_MESSAGE = 1

class ChatManagerActivity : AppCompatActivity() {

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

    private fun requestGetMatches(queue: RequestQueue, token: String, nb_matches: Int, page: Int, list_matches: ArrayList<Match>, list_messages: ArrayList<PreviewMessage>, matchAdapter: MatchAdapter, previewMessageAdapter: PreviewMessageAdapter) {
        val url = URL + "/matches"

        Log.i("my_log", "get matches request")

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                object : Response.Listener<JSONObject?>{
                     override fun onResponse(response: JSONObject?) {

                         if (response != null) {
                             val data = response.getJSONArray("data")

                             val matchesSize = list_matches.size
                             val previewMessagesSize = list_messages.size
                             for (i in 0 until data.length()) {
                                 val match = data.getJSONObject(i)

                                 // TODO A traiter quand ce sera prêt (photo + id)
                                 if (match.isNull("lastMessage")) {
                                     list_matches.add(Match())
                                 }

                                 // TODO Rajouter photo quand ce sera prêt
                                 else {
                                     val lastMessage = match.getJSONObject("lastMessage")
                                     //Log.i("my_log",ZonedDateTime.parse(lastMessage.getString("sendTime").toString()).toString())
                                     Log.i("my_log",lastMessage.getString("sendTime").toString().indexOf("+").toString())
                                     //Log.i("my_log", LocalDateTime.parse(lastMessage.getString("sendTime")).toString())
                                     list_messages.add(PreviewMessage(lastMessage.getString("message"), match.getJSONObject("user").getString("login"), (0..3).random()))
                                 }
                             }
                             matchAdapter.notifyItemInserted(matchesSize)
                             previewMessageAdapter.notifyItemInserted(previewMessagesSize)
                             
                         }
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("my_log", "error while trying to get matches\n"
                            + error.toString() + "\n"
                            + "networkResponse " + error.networkResponse + "\n"
                            + "localizedMessage " + error.localizedMessage + "\n"
                            + "message " + error.message + "\n"
                            + "cause " + error.cause + "\n"
                            + "stackTrace " + error.stackTrace.toString())
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer " + token
                return params
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject?>? {
                return try {
                    val jsonString = String(
                            response.data,
                            Charset.forName(HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET)))
                    Response.success(
                            JSONObject("{'data':$jsonString}"), HttpHeaderParser.parseCacheHeaders(response))
                } catch (e: UnsupportedEncodingException) {
                    Response.error(ParseError(e))
                } catch (je: JSONException) {
                    Response.error(ParseError(je))
                }
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun treatEmptyMatches(response: JSONObject, list_matches: ArrayList<Match>, matchAdapter: MatchAdapter) {
        val data = response.getJSONArray("data")
        val matchesSize = list_matches.size
        for (i in 0 until data.length()) {
            val match = data.getJSONObject(i)
            // TODO A traiter quand ce sera prêt (photo + id)
            if (match.isNull("lastMessage")) {
                Log.i("my_log", "match?? " + match.getString("id"))
                list_matches.add(Match())
            }
        }
        matchAdapter.notifyItemInserted(matchesSize)
    }

    private fun treatLastMessage(response: JSONObject, list_messages: ArrayList<PreviewMessage>, previewMessageAdapter: PreviewMessageAdapter) {
        val data = response.getJSONArray("data")
        val previewMessagesSize = list_messages.size
        for (i in 0 until data.length()) {
            val match = data.getJSONObject(i)

            // TODO Rajouter photo quand ce sera prêt
            val lastMessage = match.getJSONObject("lastMessage")
            list_messages.add(PreviewMessage(lastMessage.getString("message"), match.getJSONObject("user").getString("login"), (0..3).random()))
            Log.i("my_log", "message?? " + match.getString("id"))

            //Log.i("my_log",lastMessage.getString("sendTime").toString().indexOf("+").toString())
            //Log.i("my_log",ZonedDateTime.parse(lastMessage.getString("sendTime").toString()).toString())
            //Log.i("my_log", LocalDateTime.parse(lastMessage.getString("sendTime")).toString())
        }
        previewMessageAdapter.notifyItemInserted(previewMessagesSize)
    }

    private fun requestGetEmptyMatches(queue: RequestQueue, token: String, nb_matches: Int, page: Int, list_matches: ArrayList<Match>, matchAdapter: MatchAdapter) {
        val url = URL + "/matches/empty"

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                object : Response.Listener<JSONObject?>{
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            treatEmptyMatches(response, list_matches, matchAdapter)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("my_log", "error while trying to get matches\n"
                            + error.toString() + "\n"
                            + "networkResponse " + error.networkResponse + "\n"
                            + "localizedMessage " + error.localizedMessage + "\n"
                            + "message " + error.message + "\n"
                            + "cause " + error.cause + "\n"
                            + "stackTrace " + error.stackTrace.toString())
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

    private fun requestGetLastMessages(queue: RequestQueue, token: String, nb_matches: Int, page: Int, list_messages: ArrayList<PreviewMessage>, previewMessageAdapter: PreviewMessageAdapter) {
        val url = URL + "/matches/lastMsg"

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                object : Response.Listener<JSONObject?>{
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            treatLastMessage(response, list_messages, previewMessageAdapter)
                        }
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("my_log", "error while trying to get matches\n"
                            + error.toString() + "\n"
                            + "networkResponse " + error.networkResponse + "\n"
                            + "localizedMessage " + error.localizedMessage + "\n"
                            + "message " + error.message + "\n"
                            + "cause " + error.cause + "\n"
                            + "stackTrace " + error.stackTrace.toString())
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
        val previewMessagesAdapter = PreviewMessageAdapter(listMessages)
        var recyclerViewMessagePreview : RecyclerView = findViewById(R.id.previewMessagesId)
        recyclerViewMessagePreview.adapter = previewMessagesAdapter
        recyclerViewMessagePreview.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        recyclerViewMessagePreview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("my_log", newState.toString())
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load messages*/
                    requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, listMessages.size / SIZE_PAGE_PREVIEW_MESSAGE, listMessages, previewMessagesAdapter)
                    /*val size = listMessages.size
                    listMessages.add(PreviewMessage("Le refresh a fonctionné :)", "Jeanne", (0..3).random()))
                    previewMessagesAdapter.notifyItemInserted(size)*/

                }
            }
        })

        val listMatches = ArrayList<Match>()
        val matchesAdapter = MatchAdapter(listMatches)
        var recyclerViewMatches : RecyclerView = findViewById(R.id.matchRecyclerView)
        recyclerViewMatches.adapter = matchesAdapter
        recyclerViewMatches.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        recyclerViewMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("my_log", newState.toString())
                if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load matches */
                    requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, listMatches.size / SIZE_PAGE_MATCHES, listMatches, matchesAdapter)
                    /*val size = listMatches.size
                    listMatches.add(Match())
                    matchesAdapter.notifyItemInserted(size)*/

                }
            }
        })


        Log.i("my_log", "dans 2nd acti  " + token)
        requestVerifyConnect(queue, token)
        //requestGetMatches(queue, token, 4, 0, list_matches, list_messages, matchesAdapter, previewMessagesAdapter)
        requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, 0, listMatches, matchesAdapter)
        requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, 0, listMessages, previewMessagesAdapter)

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