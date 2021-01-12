package fr.uge.lootin.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById<RecyclerView>(R.id.reclyclerChat)

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("stated", newState.toString())
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----", "end");
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.i("state", "dy : " + dy)

            }
        })
        
        val tmpMessage = listOf<Chat>(Chat("yeeees", true),  Chat("go domac a 19h", false), Chat("ça fera largement l'affaire !!", false), Chat("G 5 euro", true), Chat("ça dépend si tu paies", false),Chat("yo tu baises le premier soir ?", true), Chat("Hello twa", false))
        adapter = ChatAdapter(tmpMessage, null)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)

        findViewById<Button>(R.id.sendText).setOnClickListener { sendText() }
        findViewById<Button>(R.id.picture).setOnClickListener { SendPicture() }
        findViewById<Button>(R.id.vocal).setOnClickListener { SendVocal() }

    }

    fun sendText() {
        //Toast.makeText(this, "Send message is not implemented yet", Toast.LENGTH_LONG).show()
        val message : String = findViewById<TextView>(R.id.zoneText).text.toString()
        if (message.length > 0) {
            adapter.pushFrontFirst(Chat(message, true))
            findViewById<TextView>(R.id.zoneText).setText("")
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