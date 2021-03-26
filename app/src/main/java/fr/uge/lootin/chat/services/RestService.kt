package fr.uge.lootin.chat.services

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.chat.ChatActivity
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.chat.models.MessagesResponse
import fr.uge.lootin.chat.utils.ImageUtil
import fr.uge.lootin.temporary.GsonGETRequest
import java.util.HashMap
import fr.uge.lootin.chat.ChatActivity.Companion.TAG


class RestService(private val localhost: String, private val match_id: Long, private val size_page : Long, private val adapter: ChatAdapter, private val token : String, private val idUser: Long, context : Context) {

    private val queue : RequestQueue = Volley.newRequestQueue(context)


    /**
     * Send a rest request to verify that the client is authenticated
     */
    fun verifyConnect(){
        val url = "http://$localhost:8080/showLogin"

        Log.i(TAG, "verify connexion request")
        val stringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                Log.i(TAG, "connexion OK -> Response: %s".format(response))
            },
            Response.ErrorListener { error ->
                Log.i(TAG, "error while trying to verify connexion\n"
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


    /**
     * Send a rest request to retrieve messages from a given page
     */
    fun getMessages() {
        Log.d(TAG,"GET messages")
        val page = adapter.onPage()
        val url = "http://$localhost:8080/messages/$match_id/$size_page/$page"
        Log.d(TAG, "on requête à $url")
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request =
            GsonGETRequest(url, MessagesResponse::class.java, map,
                { response ->
                    Log.i(TAG, response.toString())
                    receiveData(response)
                },
                { error -> Log.i(TAG, "error while trying to get messages\n"
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
     * Convert messages received into MessageItemUi and put them in the recyclerview
     */
    private fun receiveData(response: MessagesResponse){
        Log.d(TAG, response.toString())
        response.data.forEach{
            if (it.typeMessage == "AUDIO"){
                adapter.pushOldMessage(
                        MessageItemUi.factoryPictureItemUI(
                                ImageUtil.convert(it.message),
                                it.id,
                                it.sendTime,
                                it.sender == idUser))
            }
            else {
                adapter.pushOldMessage(
                        MessageItemUi.factoryMessageItemUI(
                                it.message,
                                it.id,
                                it.sendTime,
                                it.sender == idUser))
            }
        }
    }
}