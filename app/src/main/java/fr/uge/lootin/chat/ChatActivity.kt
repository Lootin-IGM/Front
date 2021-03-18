package fr.uge.lootin.chat

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.services.RestService
import java.util.*


class ChatActivity : AppCompatActivity() {

    /*
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter

     */


    /**
     * Checker si on est bien connecté, sinon pop up + exit(0)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // GET INFO from other activity
        val token = intent.getStringExtra(TOKEN_VALUE).toString()
        val matchId = intent.getLongExtra(MATCH_ID, -1)
        val idUser = intent.getLongExtra(USER_ID, -1)

        // Create recycler and adapter
        val recycler: RecyclerView = findViewById(R.id.reclyclerChat)
        val adapter = ChatAdapter(ArrayList())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        //create restService
        val restService = RestService(LOCALHOST, matchId, PAGE_SIZE, adapter, token, idUser)

        // Create scrollListener on recyclerview
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i(TAG, newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //TODO heheh ----------restService.getMessages()
                    recycler.scrollToPosition(0)
                }
            }
        })

        //TODO call getMessages() and look pages

        // Send Text messages
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener {
            val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
            if (message.isNotEmpty()) {
                // TODO postMessages(message)
            }
        }

        //TODO Send Picture messages (WS)
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
        }

        //TODO Send Vocal messages (WS)
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener {
            Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val TAG = "MAINACTIVITY"
        const val LOGIN = "login"
        const val PASSCODE = "passcode"

        const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
        const val MATCH_ID = "fr.uge.lootin.MATCHID"
        const val USER_ID = "fr.uge.lootin.USER_ID"

        const val PAGE_SIZE: Long = 10

        const val LOCALHOST: String = "192.168.1.58"
        const val PORT:String = "8080"
        const val ENPOINT: String = "gs-guide-websocket"
        const val TEXT_TOPIC: String = "TODO"
        const val PICTURE_TOPIC: String = "TODO"
    }

}
