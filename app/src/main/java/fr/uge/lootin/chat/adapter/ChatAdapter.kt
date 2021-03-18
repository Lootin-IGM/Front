package fr.uge.lootin.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_FRIEND_MESSAGE
import fr.uge.lootin.chat.adapter.MessageItemUi.Companion.TYPE_MY_MESSAGE

class ChatAdapter(var data: MutableList<MessageItemUi>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder<*>>() {

    abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class MyMessageViewHolder(val view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            messageContent.highlightColor = item.textColor
        }
    }

    class FriendMessageViewHolder(val view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.message)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            messageContent.highlightColor= item.textColor
        }
    }


    /**
     * TODO rajouter photos ici
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context
        return when (viewType) {
            TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.my_message, parent, false)
                MyMessageViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_message, parent, false)
                FriendMessageViewHolder(view)
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
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].messageType


    fun pushFrontFirst(chat : MessageItemUi){
        data.add(0, chat)
        notifyItemInserted(0)
    }

    fun pushOldMessage(message: MessageItemUi){
        data.add(message)
        notifyItemInserted(data.size - 1)
    }

    fun pushOldMessages(messages: List<MessageItemUi>){
        val size = data.size
        messages.forEach { data.add(it) }
        notifyItemRangeInserted(size - 1, messages.size)
    }

}