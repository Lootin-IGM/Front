package fr.uge.lootin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        findViewById<Button>(R.id.sendText).setOnClickListener { sendText() }
        findViewById<Button>(R.id.picture).setOnClickListener { SendPicture() }
        findViewById<Button>(R.id.vocal).setOnClickListener { SendVocal() }
    }

    fun sendText() {
        Toast.makeText(this, "Send message is not implemented yet", Toast.LENGTH_LONG).show()
    }

    fun SendPicture() {
        Toast.makeText(this, "Send picture is not implemented yet", Toast.LENGTH_LONG).show()
    }

    fun SendVocal() {
        Toast.makeText(this, "Send vocal is not implemented yet", Toast.LENGTH_LONG).show()
    }
}