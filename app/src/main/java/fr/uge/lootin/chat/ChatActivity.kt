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
import java.util.*

class ChatActivity : AppCompatActivity() {
    lateinit var recycler : RecyclerView
    lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycler = findViewById(R.id.reclyclerChat)
        
        val tmpMessage = listOf<Chat>(Chat("yeeees", true),  Chat("go domac a 19h", false), Chat("ça fera largement l'affaire !!", false), Chat("G 5 euro", true), Chat("ça dépend si tu paies", false),Chat("yo tu baises le premier soir ?", true), Chat("Hello twa", false))
        val mutableList : MutableList<Chat> = ArrayList()
        for(e in tmpMessage) {
            mutableList.add(e)
        }
        adapter = ChatAdapter(mutableList, null)
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