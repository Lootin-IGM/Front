package fr.uge.lootin.chat_manager


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.lootin.DefaultBadTokenHandler
import fr.uge.lootin.R
import fr.uge.lootin.chat_manager.match.Match
import fr.uge.lootin.chat_manager.match.MatchAdapter
import fr.uge.lootin.chat_manager.preview_message.PreviewMessage
import fr.uge.lootin.chat_manager.preview_message.PreviewMessageAdapter
import fr.uge.lootin.chat_manager.preview_message.TypeMessage
import fr.uge.lootin.config.Configuration
import fr.uge.lootin.httpUtils.WebRequestUtils
import org.json.JSONObject


class ChatManagerFragment : Fragment() {
    val chatManagerReceiver =  object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            TODO("Not yet implemented")
            Log.i("my_log", "on a reçu")
            /*
            var frg : Fragment? = activity!!.supportFragmentManager.findFragmentByTag("chatManagerFragment")
            val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()

            ft.detach(frg!!)
            ft.attach(frg)
            ft.commit()*/
        }
    }

    private var token: String = ""
    private var baseUrl = ""
    private var contextActivity: Context? = null

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
                WebRequestUtils.onError(error)
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
                } else {
                    Thread.sleep(10000)
                    requestVerifyConnect(queue, token)
                }
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
    private fun treatEmptyMatches(
        response: JSONObject,
        list_matches: ArrayList<Match>,
        matchAdapter: MatchAdapter,
        page: Int,
        layout: View
    ) {
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
                list_matches.add(
                    Match(
                        Integer.valueOf(match.getString("id")),
                        match.getJSONObject("user").getString("login"),
                        Integer.valueOf(match.getJSONObject("user").getString("id")),
                        fromStringToBitmap(match.getJSONObject("user").getString("image"))
                    )
                )
            }
        }
        if (matchesSize == 0 && list_matches.size > 0) {
            layout.findViewById<CardView>(R.id.loadingPanelMatches).visibility = View.GONE
            layout.findViewById<RecyclerView>(R.id.matchRecyclerView).visibility = View.VISIBLE
        }
        matchAdapter.notifyItemInserted(matchesSize)
    }

    private fun treatLastMessage(
        response: JSONObject,
        list_messages: ArrayList<PreviewMessage>,
        previewMessageAdapter: PreviewMessageAdapter,
        page: Int,
        layout: View
    ) {
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
            var type: TypeMessage = TypeMessage.TEXT
            //TODO A CHANGER!!!
            if (lastMessage.getString("typeMessage").equals("PICTURE")) {
                type = TypeMessage.PHOTO
            }
            list_messages.add(
                PreviewMessage(
                    Integer.valueOf(match.getString("id")),
                    lastMessage.getString(
                        "message"
                    ),
                    match.getJSONObject("user").getString("login"),
                    Integer.valueOf(
                        match.getJSONObject("user").getString(
                            "id"
                        )
                    ),
                    fromStringToBitmap(match.getJSONObject("user").getString("image")),
                    lastMessage.getString(
                        "sendTime"
                    ),
                    type
                )
            )

        }
        if (previewMessagesSize == 0 && list_messages.size > 0) {
            layout.findViewById<CardView>(R.id.loadingPanelChatManager).visibility = View.GONE
            layout.findViewById<RecyclerView>(R.id.previewMessagesId).visibility = View.VISIBLE
        }
        previewMessageAdapter.notifyItemInserted(previewMessagesSize)
    }

    private fun requestGetEmptyMatches(
        queue: RequestQueue,
        token: String,
        nb_matches: Int,
        list_matches: ArrayList<Match>,
        matchAdapter: MatchAdapter,
        layout: View
    ) {
        val url = baseUrl + "/matches/empty"
        var page = list_matches.size / SIZE_PAGE_MATCHES
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
            object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    if (response != null) {
                        treatEmptyMatches(response, list_matches, matchAdapter, page, layout)
                    }
                }
            },
            Response.ErrorListener { error ->
                WebRequestUtils.onError(error)
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
                } else {
                    layout.findViewById<CardView>(R.id.retryPanelMatches).visibility = View.VISIBLE
                    layout.findViewById<CardView>(R.id.loadingPanelMatches).visibility = View.GONE
                    layout.findViewById<RecyclerView>(R.id.matchRecyclerView).visibility = View.GONE

                    layout.findViewById<Button>(R.id.retryMatches).setOnClickListener {
                        Log.i("my_log", "on a cliqué")
                        layout.findViewById<CardView>(R.id.retryPanelMatches).visibility = View.GONE
                        layout.findViewById<CardView>(R.id.loadingPanelMatches).visibility =
                            View.VISIBLE
                        requestGetEmptyMatches(
                            queue,
                            token,
                            nb_matches,
                            list_matches,
                            matchAdapter,
                            layout
                        )
                    }
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun requestGetLastMessages(
        queue: RequestQueue,
        token: String,
        nb_matches: Int,
        list_messages: ArrayList<PreviewMessage>,
        previewMessageAdapter: PreviewMessageAdapter,
        layout: View
    ) {
        val url = baseUrl + "/matches/lastMsg"

        var page = list_messages.size / SIZE_PAGE_PREVIEW_MESSAGE
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject("{\"nbMatches\": " + nb_matches + ",\"page\":" + page + "}"),
            object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    if (response != null) {
                        treatLastMessage(
                            response,
                            list_messages,
                            previewMessageAdapter,
                            page,
                            layout
                        )
                    }
                }
            },
            Response.ErrorListener { error ->
                WebRequestUtils.onError(error)
                if (error is AuthFailureError) {
                    DefaultBadTokenHandler.handleBadRequest(contextActivity!!)
                } else {
                    layout.findViewById<CardView>(R.id.retryPanelPreviewMessage).visibility =
                        View.VISIBLE
                    layout.findViewById<CardView>(R.id.loadingPanelChatManager).visibility =
                        View.GONE
                    layout.findViewById<RecyclerView>(R.id.previewMessagesId).visibility = View.GONE

                    layout.findViewById<Button>(R.id.retryPreviewMessage).setOnClickListener {
                        Log.i("my_log", "on a cliqué")
                        layout.findViewById<CardView>(R.id.retryPanelPreviewMessage).visibility =
                            View.GONE
                        layout.findViewById<CardView>(R.id.loadingPanelChatManager).visibility =
                            View.VISIBLE
                        requestGetLastMessages(
                            queue,
                            token,
                            nb_matches,
                            list_messages,
                            previewMessageAdapter,
                            layout
                        )
                    }
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $token"
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contextActivity = context
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
        val previewMessagesAdapter = PreviewMessageAdapter(listMessages, requireActivity())
        var recyclerViewMessagePreview : RecyclerView = layout.findViewById(R.id.previewMessagesId)
        recyclerViewMessagePreview.adapter = previewMessagesAdapter
        recyclerViewMessagePreview.layoutManager = GridLayoutManager(
            activity?.applicationContext,
            1,
            RecyclerView.VERTICAL,
            false
        )
        recyclerViewMessagePreview.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    requestGetLastMessages(
                        queue,
                        token,
                        SIZE_PAGE_PREVIEW_MESSAGE,
                        listMessages,
                        previewMessagesAdapter,
                        layout
                    )
                }
            }
        })

        val listMatches = ArrayList<Match>()
        val matchesAdapter = MatchAdapter(listMatches, requireActivity())
        var recyclerViewMatches : RecyclerView = layout.findViewById(R.id.matchRecyclerView)
        recyclerViewMatches.adapter = matchesAdapter
        recyclerViewMatches.layoutManager = GridLayoutManager(
            activity?.applicationContext,
            1,
            RecyclerView.HORIZONTAL,
            false
        )
        recyclerViewMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    requestGetEmptyMatches(
                        queue,
                        token,
                        SIZE_PAGE_MATCHES,
                        listMatches,
                        matchesAdapter,
                        layout
                    )
                }
            }
        })

        requestVerifyConnect(queue, token)
        requestGetEmptyMatches(queue, token, SIZE_PAGE_MATCHES, listMatches, matchesAdapter, layout)
        requestGetLastMessages(
            queue,
            token,
            SIZE_PAGE_PREVIEW_MESSAGE,
            listMessages,
            previewMessagesAdapter,
            layout
        )

        val intentFilter = IntentFilter()
        //intentFilter.addAction()
        activity?.registerReceiver(chatManagerReceiver, intentFilter)

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
