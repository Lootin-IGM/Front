package fr.uge.lootin.chat_manager

import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.*
import com.android.volley.toolbox.HttpHeaderParser
import fr.uge.lootin.R
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


//import iammert.com.expandablelib.ExpandableLayout
//import iammert.com.expandablelib.Section

class ChatManagerActivity : AppCompatActivity() {
    /*

    //var url = "http://localhost:8080/login"
        val url = "http://192.168.43.2:8080/login"
        //var url = "http://192.168.43.2:80/showLogin"

        Log.i("my_log", "encore avant")

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"username\": \"Loulou\",\"password\": \"Yvette\"}"),
                Response.Listener { response ->
                    Log.i("my_log", "Response: %s".format(response.toString()))
                },
                Response.ErrorListener { error ->
                    Log.i("my_log", "errooooooooooooooooooooooooooooooorrrrrrr!!!!!!!!!!!!!!!!!!!!!\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString())
                }
        )
        /*
        val stringRequest: StringRequest =
                StringRequest(
                        Request.Method.GET, url,
                        Response.Listener { response ->

                                Log.i("my_log", "c'est bon " + response.toString())
                                //findViewById<TextView>(R.id.textResult).text = "yes"


                        },
                        Response.ErrorListener { error ->
                            Log.i("my_log", "errooooooooooooooooooooooooooooooorrrrrrr!!!!!!!!!!!!!!!!!!!!!\n" + error.toString() + "\n" + error.networkResponse)
                        })*/

     */

    private fun verifyConnect(queue: RequestQueue, token: String){
        val url = "http://192.168.43.2:8080/showLogin"

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

    private fun getMatches(queue: RequestQueue, token: String, nb_matches: Int, page: Int) {
        val url = "http://192.168.43.2:8080/matches"

        Log.i("my_log", "get matches request")


        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
                Response.Listener { response ->
                    Log.i("my_log", "Response: %s".format(response.toString()))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_manager)
        /*
        val layout : ExpandableLayout = findViewById(R.id.expandable_layout)
        val renderer = MyRenderer()
        layout.setRenderer(renderer)*/

        val list_messages = ArrayList<PreviewMessage>()
        list_messages.add(PreviewMessage("Heyyy", "Jeanne", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy ça va?", "Jeanne2", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne3", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne4", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne5", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne6", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne7", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne8", (0..3).random()))
        list_messages.add(PreviewMessage("Heyyyzerht", "Jeanne9", (0..3).random()))
        val previewMessagesAdapter = PreviewMessageAdapter(list_messages)
        var recyclerViewMessagePreview : RecyclerView = findViewById(R.id.previewMessagesId)
        recyclerViewMessagePreview.adapter = previewMessagesAdapter
        recyclerViewMessagePreview.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)

        val list_matches = ArrayList<Match>()
        for(i in 0..10)
            list_matches.add(Match())
        val matchesAdapter = MatchAdapter(list_matches)
        var recyclerViewMatches : RecyclerView = findViewById(R.id.matchRecyclerView)
        recyclerViewMatches.adapter = matchesAdapter
        recyclerViewMatches.layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)

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
        Log.i("my_log", "deprecated method 2 : " + height2 + "     " + width2)

        val queue = Volley.newRequestQueue(this)
        val token: String? = intent.getStringExtra(TOKEN_VALUE)
        if (token != null) {
            Log.i("my_log", "dans 2nd acti  " + token)
            verifyConnect(queue, token)
            getMatches(queue, token, 4, 0)
        }



    }

    

}