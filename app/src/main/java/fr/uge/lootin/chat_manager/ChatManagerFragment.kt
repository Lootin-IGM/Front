package fr.uge.lootin.chat_manager


import fr.uge.lootin.config.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import fr.uge.lootin.ProfilesSwipingActivity
import fr.uge.lootin.R
import fr.uge.lootin.settings.*
import fr.uge.lootin.chat_manager.ChatManagerActivity
import fr.uge.lootin.chat_manager.match.Match
import fr.uge.lootin.chat_manager.match.MatchAdapter
import fr.uge.lootin.chat_manager.preview_message.PreviewMessage
import fr.uge.lootin.chat_manager.preview_message.PreviewMessageAdapter
import fr.uge.lootin.config.Configuration.Companion.getUrl
import org.json.JSONObject

class ChatManagerFragment : Fragment () {


    private var token: String = ""
    private var baseUrl = ""


    /*
        private fun launchGameListFragment() {
            val gamesSettingsFrag = GamesList.settingsInstance(token)
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction().remove(this)
                .commit()
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, gamesSettingsFrag, "gamesSettingsFragment")
                .addToBackStack("gamesSettingsFragment").commit()
        }

        private fun launchProfilePictureFragment() {
            val profileFragment = TakePicture.settingsInstance(token)
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction().remove(this)
                .commit()
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, profileFragment, "pictureSettingsFragment")
                .addToBackStack("pictureSettingsFragment").commit()
        }

        private fun launchDescriptionFragment() {
            val descriptionFragment = Description.settingsInstance(token)
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction().remove(this)
                .commit()
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, descriptionFragment, "descriptionSettingsFragment")
                .addToBackStack("descriptionSettingsFragment").commit()
        }

        private fun closeSettingsFragment() {
            val emptyFragment = EmptyFragment()
            (activity as ChatManagerActivity).supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_r_l,
                    R.anim.fade_out_r_l, R.anim.fade_in_r_l, R.anim.slide_out_r_l
                ).replace(R.id.fragment_container_view, emptyFragment, "emptyFragment")
                .remove(emptyFragment).commit()
        }*/
    private fun fromStringToBitmap(image: String) : Bitmap {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun requestVerifyConnect(queue: RequestQueue, token: String){
        val url = baseUrl + "/showLogin"

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
            list_messages.add(PreviewMessage(Integer.valueOf(match.getString("id")), lastMessage.getString("message"), match.getJSONObject("user").getString("login"), Integer.valueOf(match.getJSONObject("user").getString("id")), fromStringToBitmap(match.getJSONObject("user").getString("image")), lastMessage.getString("sendTime")))

        }
        previewMessageAdapter.notifyItemInserted(previewMessagesSize)
    }

    private fun requestGetEmptyMatches(queue: RequestQueue, token: String, nb_matches: Int, page: Int, list_matches: ArrayList<Match>, matchAdapter: MatchAdapter) {
        val url = baseUrl + "/matches/empty"
        var page = list_matches.size / SIZE_PAGE_MATCHES
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
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
        val url = baseUrl + "/matches/lastMsg"

        var page = list_messages.size / SIZE_PAGE_PREVIEW_MESSAGE
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
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


    private fun closeChatManager() {
        activity?.supportFragmentManager?.popBackStack()
    }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            baseUrl = Configuration.getUrl(activity?.applicationContext!!)
            token = requireArguments().getString("token").toString()

            val layout = inflater.inflate(R.layout.activity_chat_manager, container, false)
            layout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
                closeChatManager()
            }


            val queue = Volley.newRequestQueue(activity?.applicationContext)

            val listMessages = ArrayList<PreviewMessage>()
            val previewMessagesAdapter = PreviewMessageAdapter(listMessages)
            var recyclerViewMessagePreview : RecyclerView = layout.findViewById(R.id.previewMessagesId)
            recyclerViewMessagePreview.adapter = previewMessagesAdapter
            recyclerViewMessagePreview.layoutManager = GridLayoutManager(activity?.applicationContext, 1, RecyclerView.VERTICAL, false)
            recyclerViewMessagePreview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, listMessages, previewMessagesAdapter)
                    }
                }
            })

            val listMatches = ArrayList<Match>()
            val matchesAdapter = MatchAdapter(listMatches)
            var recyclerViewMatches : RecyclerView = layout.findViewById(R.id.matchRecyclerView)
            recyclerViewMatches.adapter = matchesAdapter
            recyclerViewMatches.layoutManager = GridLayoutManager(activity?.applicationContext, 1, RecyclerView.HORIZONTAL, false)
            recyclerViewMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, listMatches.size / SIZE_PAGE_MATCHES, listMatches, matchesAdapter)
                    }
                }
            })

            requestVerifyConnect(queue, token)
            requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, 0, listMatches, matchesAdapter)
            requestGetLastMessages(queue, token, SIZE_PAGE_PREVIEW_MESSAGE, listMessages, previewMessagesAdapter)



            return layout
        }

        companion object {
            fun newInstance(token: String): fr.uge.lootin.chat_manager.ChatManagerFragment {
                var fragment = fr.uge.lootin.chat_manager.ChatManagerFragment()
                val args = Bundle()
                args.putString("token", token)
                fragment.arguments = args
                return fragment
            }
        }
}
