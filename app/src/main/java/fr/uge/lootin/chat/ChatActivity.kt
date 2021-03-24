package fr.uge.lootin.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.ChatAdapter
import fr.uge.lootin.chat.services.MessageTextService
import java.io.ByteArrayOutputStream
import java.util.*


class ChatActivity : AppCompatActivity() {

    lateinit var messageService : MessageTextService
    lateinit var recycler: RecyclerView
    lateinit var adapter: ChatAdapter
    var idUser : Long = 0
    var matchId: Long = -1
    var sendTo : Long = 1

    /**
     * Checker si on est bien connecté, sinon pop up + exit(0)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // GET INFO from other activity
        val token = intent.getStringExtra(TOKEN_VALUE).toString()
        //matchId = intent.getLongExtra(MATCH_ID, -1)
        //val idUser = 1L //intent.getLongExtra(USER_ID, -1)
        //sendTo = 1L

        // Create recycler and adapter
        recycler= findViewById(R.id.reclyclerChat)
        adapter = ChatAdapter(ArrayList(), PAGE_SIZE)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        //create restService
        //TODO val restService = RestService(LOCALHOST, matchId, PAGE_SIZE, adapter, token, idUser)

        //create web sockets services
        messageService = MessageTextService(adapter, recycler, this, "ws://$LOCALHOST:$PORT/$ENPOINT", idUser)

        // Create scrollListener on recyclerview
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i(TAG, newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //TODO restService.getMessages()
                    //recycler.scrollToPosition(adapter.getItemCount() - 1)
                }
            }
        })

        //TODO restService.getMessages()
        messageService.createStomp()

        // Send Text messages
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener {
            val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
            if (message.isNotEmpty()) {
                messageService.sendMessage(message, matchId, sendTo)
                findViewById<TextView>(R.id.zoneText).text = ""
                //recycler.scrollToPosition(adapter.getItemCount() - 1)
            }
        }

        //TODO Send Picture messages (WS)
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(this, "Send my picture", Toast.LENGTH_LONG).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 200)
        }

        //TODO Send camera picture messages (WS)
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener {
            Toast.makeText(this, "Send camera pictures", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }
    }

    /**
     * retour en fonction de la galerie ou de la capture
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var res: Bitmap? = null
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null) {
             res  = data.extras!!.get("data") as Bitmap
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            res = (data?.data as BitmapDrawable).bitmap // to get bitmap from imageView
        }

        val stream = ByteArrayOutputStream()
        res?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val array = stream.toByteArray()

        val base64String: String? = res?.let { ImageUtil.convert(it) }

        //send picture with web socket
        if (array != null) {
            messageService.sendPicture(base64String, matchId , sendTo)
            recycler.scrollToPosition(0)
        }
    }

    companion object {
        private const val TAG = "--ACTIVITY--MAIN"
        const val LOGIN = "login"
        const val PASSCODE = "passcode"

        const val TOKEN_VALUE = "fr.uge.lootin.TOKEN"
        const val MATCH_ID = "fr.uge.lootin.MATCHID"
        const val USER_ID = "fr.uge.lootin.USER_ID"

        const val PAGE_SIZE: Long = 10

        const val LOCALHOST: String = "192.168.1.58"
        const val PORT:String = "8080"
        const val ENPOINT: String = "secured/room"

        //const val ENPOINT: String = "gs-guide-websocket"
    }
}
