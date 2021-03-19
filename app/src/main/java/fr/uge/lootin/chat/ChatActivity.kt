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
import fr.uge.lootin.chat.services.MessageService
import fr.uge.lootin.chat.services.RestService
import java.io.ByteArrayOutputStream
import java.util.*


class ChatActivity : AppCompatActivity() {

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
        val adapter = ChatAdapter(ArrayList(), PAGE_SIZE)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        //create restService
        val restService = RestService(LOCALHOST, matchId, PAGE_SIZE, adapter, token, idUser)

        //create web sockets services
        val messageService = MessageService(adapter, recycler, this, "ws://$LOCALHOST:$PORT/$ENPOINT/websocket", idUser)

        // Create scrollListener on recyclerview
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i(TAG, newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    restService.getMessages()
                    recycler.scrollToPosition(0)
                }
            }
        })

        restService.getMessages()
        messageService.createStomp()

        // Send Text messages
        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener {
            val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
            if (message.isNotEmpty()) {
                // TODO postMessages(message)
            }
        }

        //TODO Send Picture messages (WS)
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener {
            Toast.makeText(this, "Send camera picture", Toast.LENGTH_LONG).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 200)
        }

        //TODO Send Vocal messages (WS)
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener {
            Toast.makeText(this, "Send my pictures", Toast.LENGTH_LONG).show()
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
        //TODO send array au websocket


        /**
         * TODO FOR DECODE ARRAY
         *
         * Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray .length);
         * imageView.setImageBitmap(bitmap);
         */
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
