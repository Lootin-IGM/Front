package fr.uge.lootin.chat_manager

import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.R
import org.json.JSONObject
import kotlin.jvm.Throws


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

    private fun connect(queue: RequestQueue): String{
        val url = "http://192.168.43.2:8080/login"
        Log.i("my_log", "connect request")
        var token: String = ""
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject("{\"username\": \"Loulou\",\"password\": \"Yvette\"}"),
                Response.Listener { response ->
                    Log.i("my_log", "Connect Response: %s".format(response.toString()));
                    val jsonResponse = JSONObject(response.toString());
                    token = jsonResponse.getString("jwt")

                },
                Response.ErrorListener { error ->
                    Log.i("my_log", "error while trying to connect\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString())
                }
        )
        queue.add(jsonObjectRequest)
        return token
    }

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
                Log.i("my_log", token);
                params["Authorization"] = "Bearer " + token
                return params
            }
        }

        queue.add(stringRequest)
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

        Log.i("my_log", "chatManager.height and width: " +  chatManager.height + "     " + chatManager.width)
        Log.i("my_log", "chatManager.measuredHeight and measuredWidth: " +  chatManager.measuredWidth + "     " + chatManager.measuredHeight)
        Log.i("my_log", "chatManager.bacckgroundColor " +  chatManager.background)

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
            Log.i("my_log", "dans 2nd acti" + token)
            verifyConnect(queue, token)
        }

    }

    

}