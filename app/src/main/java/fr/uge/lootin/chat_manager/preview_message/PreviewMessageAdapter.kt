package fr.uge.lootin.chat_manager.preview_message

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import fr.uge.lootin.R
import java.lang.reflect.Type

class PreviewMessageAdapter (private var previewMessages: ArrayList<PreviewMessage>) : RecyclerView.Adapter<PreviewMessageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var message: TextView = itemView.findViewById(R.id.last_message)
        private var pseudo: TextView = itemView.findViewById(R.id.pseudo)
        private var layout: ConstraintLayout = itemView.findViewById(R.id.layoutId)
        private var photo: CircleImageView = itemView.findViewById(R.id.conversation_photo)
        fun update(newMessage: String, newPseudo: String, color: Int, image: Bitmap, type: TypeMessage) {
            if (type == TypeMessage.PHOTO) {
                message.text = "[Photo]"
            }
            else {
                message.text = newMessage
            }
            pseudo.text = newPseudo
            layout.setBackgroundColor(color)
            photo.setImageBitmap(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val vh = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.preview_message_layout, parent, false)
        )
        return vh
    }

    override fun getItemCount(): Int {
        return previewMessages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var color: Int
        if (position % 2 == 0) {
            color = Color.parseColor("#1F2124")
        }
        else {
            color = Color.parseColor("#2C2F33")
        }
        holder.update(previewMessages[position].message, previewMessages[position].sender, color, previewMessages[position].photo, previewMessages[position].type)
        holder.itemView.setOnClickListener { Log.i("my_log", "on a cliqué sur message: " + previewMessages[position].sender + " - " + previewMessages[position].message) }

    }
}