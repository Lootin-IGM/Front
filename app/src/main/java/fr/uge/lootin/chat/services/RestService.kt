package fr.uge.lootin.chat.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.DefaultBadTokenHandler
import fr.uge.lootin.chat.ChatFragment
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.adapter.MessageItemUi
import fr.uge.lootin.chat.models.MessagesResponse
import fr.uge.lootin.chat.utils.ImageUtil
import fr.uge.lootin.temporary.GsonGETRequest
import java.util.HashMap
import fr.uge.lootin.chat.ChatFragment.Companion.TAG
import fr.uge.lootin.form.FileDataPart
import fr.uge.lootin.form.VolleyFileUploadRequest
import fr.uge.lootin.httpUtils.WebRequestUtils
import fr.uge.lootin.models.UserFull
import fr.uge.lootin.models.UserList
import fr.uge.lootin.models.Users
import java.io.ByteArrayOutputStream


class RestService(private val localhost: String, private val port: String, private val match_id: Long, private val size_page : Long, private val adapter: ChatAdapter, private val token : String, private val idUser: Long, private val context : Context, ) {

    private val queue : RequestQueue = Volley.newRequestQueue(context)


    /**
     * Send a rest request to verify that the client is authenticated
     */
    fun verifyConnect(){
        val url = "http://$localhost:$port/showLogin"

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
        val url = "http://$localhost:$port/messages/$match_id/$size_page/$page"
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
     * Send a rest request to retrieve messages from a given page
     */
    fun getPicture(id : Long, match : Long, recycler : RecyclerView) {
        Log.d(TAG,"GET picture")
        val url = "http://$localhost:$port/messages/picture/$match/$id/"
        Log.d(TAG, "on requête à $url")
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request =
            GsonGETRequest(url, MessagesResponse.Message::class.java, map,
                { response ->
                    Log.i(TAG, response.toString())
                    receivePicture(response, recycler)
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

    fun getUser(otherUser : Long, chatFragment: ChatFragment) {
        val url = "http://$localhost:$port/profile/full/$otherUser"
        val map = HashMap<String, String>()
        map["Authorization"] = "Bearer $token"
        val request =
            GsonGETRequest(url, Users::class.java, map,
            { response ->
                WebRequestUtils.onResult(response)
                chatFragment.displayUser(response)
            },
            { error -> Log.i(TAG, "error while trying to get user\n"
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


     fun sendPictureRequest(picture: Bitmap) {
        val url = "http://$localhost:$port/picture"
        Log.i("test", "post send picture request")
        val jsonObjectRequest = object : VolleyFileUploadRequest(
            Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(context, "Sending picture", Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener { error ->
                Log.i(
                    "test", "error while trying to connect\n"
                            + error.toString() + "\n"
                            + error.networkResponse + "\n"
                            + error.localizedMessage + "\n"
                            + error.message + "\n"
                            + error.cause + "\n"
                            + error.stackTrace.toString()
                )
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(context)
                }
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val stream = ByteArrayOutputStream()
                picture.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                params["picture"] = FileDataPart("file", stream.toByteArray(), "jpeg")
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                //Log.i("test", "username=$username, password=$password, firstname=$firstName, lastname=$lastName, game=${games.joinToString()}, age=${age.toString()}, gender=$gender, attraction=$attraction")
                params["matchId"] = "$match_id"
                params["sender"] = "$idUser"

                return params
            }
        }

        queue.add(jsonObjectRequest)
    }

    /**
     * Convert messages received into MessageItemUi and put them in the recyclerview
     */
    private fun receiveData(response: MessagesResponse){
        Log.d(TAG, response.toString())
        response.data.forEach{
            if (it.typeMessage == "PICTURE"){
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

    private fun receivePicture(response: MessagesResponse.Message, recycler: RecyclerView){
        Log.d(TAG, response.toString())
        if (response.typeMessage == "PICTURE"){
            adapter.pushMessage(
                MessageItemUi.factoryPictureItemUI(
                    ImageUtil.convert(response.message),
                    response.id,
                    response.sendTime,
                    response.sender == idUser))
            recycler.scrollToPosition(0)

        }
    }
}