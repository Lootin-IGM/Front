package fr.uge.lootin.chat.adapter

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_FRIEND_MESSAGE
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_FRIEND_MESSAGE_PICTURE
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_MY_MESSAGE
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_MY_MESSAGE_PICTURE
import java.lang.Long.max


class ChatAdapter(var data: MutableList<MessageItemUi>, private val size_page: Long) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder<*>>() {

    abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    /**
     * Viewholder of the text message the client is sending
     */
    class MyMessageViewHolder(view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)
        private val date = view.findViewById<TextView>(R.id.date_send)

        override fun bind(item: MessageItemUi) {

            messageContent.text = item.content
            messageContent.highlightColor = item.textColor
            date.text = item.date

        }
    }

    /**
     * Viewholder of the text message the friend's client is sending
     */
    class FriendMessageViewHolder(view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)
        private val date = view.findViewById<TextView>(R.id.date_send)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            messageContent.highlightColor= item.textColor
            date.text = item.date
        }
    }

    /**
     * Viewholder of the Picture message the client is sending
     */
    class MyPictureViewHolder(view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val picture = view.findViewById<ImageView>(R.id.picture)
        private val date = view.findViewById<TextView>(R.id.date_send)

        override fun bind(item: MessageItemUi) {
            val bitmap = item.picture
            if (bitmap != null) {
                val scaled = Bitmap.createScaledBitmap(
                    bitmap, 512,
                    ((bitmap.height * (512.0 / bitmap.width)).toInt()), true
                )
                picture.setImageBitmap(scaled)            }
            //picture.setImageBitmap(item.picture)
            date.text = item.date
        }
    }

    /**
     * Viewholder of the Picture message the friend's client is sending
     */
    class FriendPictureViewHolder(view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val picture = view.findViewById<ImageView>(R.id.picture)
        private val date = view.findViewById<TextView>(R.id.date_send)

        override fun bind(item: MessageItemUi) {
            val bitmap = item.picture
            if (bitmap != null) {
                val scaled = Bitmap.createScaledBitmap(
                    bitmap, 512,
                    ((bitmap.height * (512.0 / bitmap.width)).toInt()), true
                )
                picture.setImageBitmap(scaled)
            }
            date.text = item.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context
        return when (viewType) {
            TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(
                    R.layout.my_message_text,
                    parent,
                    false
                )
                MyMessageViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.friend_message_text,
                    parent,
                    false
                )
                FriendMessageViewHolder(view)
            }
            TYPE_MY_MESSAGE_PICTURE -> {
                val view = LayoutInflater.from(context).inflate(
                    R.layout.my_message_picture,
                    parent,
                    false
                )
                MyPictureViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE_PICTURE -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.friend_message_picture,
                    parent,
                    false
                )
                FriendPictureViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int) {
        val item = data[position]
        Log.d("adapter View", position.toString() + item.content)
        when (holder) {
            is MyMessageViewHolder -> holder.bind(item)
            is FriendMessageViewHolder -> holder.bind(item)
            is MyPictureViewHolder -> holder.bind(item)
            is FriendPictureViewHolder -> holder.bind(item)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].messageType


    /**
     * Add old messages in the adapter
     */
    fun pushOldMessage(message: MessageItemUi){
        if(!data.contains(message)){
            data.add(message)
            notifyItemInserted(data.size - 1)
        }
    }

    /**
     * Add new messages in the adapter
     */
    fun pushMessage(message: MessageItemUi){
        data.add(0, message)
        notifyItemInserted(0)
    }

    /**
     * Return the current page
     */
    fun onPage(): Long{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return max(data.size / size_page, 0L)
        }
        return 0;
    }
}