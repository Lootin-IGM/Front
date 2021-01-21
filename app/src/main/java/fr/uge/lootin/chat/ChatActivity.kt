package fr.uge.lootin.chat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)

        
        val tmpMessage = listOf<MessageItemUi>( MessageItemUi("j'adore manger", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE), MessageItemUi("yeeees", Color.DKGRAY, MessageItemUi.TYPE_MY_MESSAGE), MessageItemUi("go domac a 19h", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE) ,  MessageItemUi("Hey", Color.DKGRAY, MessageItemUi.TYPE_FRIEND_MESSAGE),  MessageItemUi("Yo, on se capte ce soir ?", Color.DKGRAY, MessageItemUi.TYPE_MY_MESSAGE))
        val mutableList : MutableList<MessageItemUi> = ArrayList()
        for(e in tmpMessage) {
            mutableList.add(e)
        }
        adapter = ChatAdapter(mutableList)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("stated", newState.toString())
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*TODO load messages*/
                    Log.d("-----", "end");
                }
            }
        })


        findViewById<ImageButton>(R.id.imageButtonsendText).setOnClickListener { sendText() }
        findViewById<ImageButton>(R.id.imageButtonPicture).setOnClickListener { SendPicture() }
        findViewById<ImageButton>(R.id.imageButtoncamera).setOnClickListener { SendVocal() }


    }



    fun sendText() {
        //Toast.makeText(this, "Send message is not implemented yet", Toast.LENGTH_LONG).show()
        val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
        if (message.isNotEmpty()) {
            adapter.pushFrontFirst(MessageItemUi(message,  Color.WHITE, MessageItemUi.TYPE_MY_MESSAGE))
            findViewById<TextView>(R.id.zoneText).text = ""
            recycler.scrollToPosition(0)
        }
    }

    fun SendPicture() {
        Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
    }

    fun SendVocal() {
        Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
    }


}