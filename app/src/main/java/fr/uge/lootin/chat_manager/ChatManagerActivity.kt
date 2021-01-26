package fr.uge.lootin.chat_manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat_manager.model.MyRenderer
import fr.uge.lootin.chat_manager.model.Phone
import fr.uge.lootin.chat_manager.model.PhoneCategory
import iammert.com.expandablelib.ExpandableLayout
import iammert.com.expandablelib.Section

class ChatManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_manager)

        val layout : ExpandableLayout = findViewById(R.id.expandable_layout)
        val renderer = MyRenderer()
        layout.setRenderer(renderer)

        val list_messages = ArrayList<PreviewMessage>()
        list_messages.add(PreviewMessage("Heyyy", "Jeanne"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne2"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne3"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne4"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne5"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne6"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne7"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne8"))
        list_messages.add(PreviewMessage("Heyyy", "Jeanne9"))
        val previewMessagesAdapter = PreviewMessageAdapter(list_messages)
        var recyclerViewMessagePreview : RecyclerView = findViewById(R.id.previewMessagesId)
        recyclerViewMessagePreview.adapter = previewMessagesAdapter
        recyclerViewMessagePreview.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
    }

    

}