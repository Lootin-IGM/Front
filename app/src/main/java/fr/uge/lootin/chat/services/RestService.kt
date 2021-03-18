package fr.uge.lootin.chat.services

import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.dto.MessagesResponse
import fr.uge.lootin.request.GsonGETRequest
import org.json.JSONObject
import java.util.HashMap

class RestService(private val localhost: String, private val match_id: String, private val size_page : String, private val adapter: ChatAdapter) {
    private lateinit var token: String
    private lateinit var queue : RequestQueue
    private var page: Int = 0

    fun verifyConnect(queue: RequestQueue, token: String){
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



    fun connect(queue: RequestQueue) {
        val url = "http://192.168.1.44:8080/login"
        Log.i("my_log", "connect request")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject("{\"username\": \"Loulou\",\"password\": \"Yvette\"}"),
            { response ->
                Log.i("my_log", "Connect Response: %s".format(response.toString()));
                val jsonResponse = JSONObject(response.toString());
                this.token = jsonResponse.getString("jwt")

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

    fun getMessages() {
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

    private fun receiveData(response: MessagesResponse){

        response.data.forEach{adapter.pushOldMessage(MessageItemUi(it.message, Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE, it.id, it.sendTime))}
    }
}