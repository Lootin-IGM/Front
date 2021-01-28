package fr.uge.lootin.chat_manager

import android.graphics.Color
import android.icu.number.IntegerWidth
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R

class PreviewMessageAdapter (private var previewMessages: ArrayList<PreviewMessage>) : RecyclerView.Adapter<PreviewMessageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var message: TextView = itemView.findViewById(R.id.last_message)
        private var pseudo: TextView = itemView.findViewById(R.id.pseudo)
        private var layout: ConstraintLayout = itemView.findViewById(R.id.layoutId)
        fun update(newMessage: String, newPseudo: String, color: Int) {
            message.text = newMessage
            pseudo.text = newPseudo
            layout.setBackgroundColor(color)
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
        holder.update(previewMessages[position].message, previewMessages[position].sender, color)

    }
}