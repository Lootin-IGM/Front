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

class RestService(private val localhost: String, private val match_id: Long, private val size_page : Long, private val adapter: ChatAdapter, private val token : String) {

    private lateinit var queue : RequestQueue

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



    fun getMessages(page : Long) {
        val url = "http://$localhost:8080/messages/$match_id/$size_page/$page"
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

    /**
     * TODO mettre l'id du bg qui envoie les messages
     */
    private fun receiveData(response: MessagesResponse){
        response.data.forEach{adapter.pushOldMessage( MessageItemUi.factoryMessageItemUI(it.message, it.id, it.sendTime,it.sender.id == 0L ))}
    }
}