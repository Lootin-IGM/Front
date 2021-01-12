package fr.uge.lootin.chat

import android.annotation.SuppressLint
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import fr.uge.lootin.R
import java.util.function.Consumer


class ChatAdapter(var messages: List<Chat>, var listener: Consumer<Int>?) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {


        val messageLeft = itemView.findViewById<TextView>(R.id.left)
        val messageRight = itemView.findViewById<TextView>(R.id.right)


        @SuppressLint("ResourceType")
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun update(item: Chat, pos: Int) {

            if(item.iSend){
                messageRight.setText(item.message)
            } else {
                messageLeft.setText(item.message)
            }
            itemView.setOnClickListener { v ->
                listener!!.accept(pos)
                notifyItemChanged(pos)

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.update(messages[i], i)
    }

    override fun getItemCount(): Int {
        return messages.size
    }


    @Override
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(
                        R.layout.message,
                        viewGroup,
                        false
                )
        )
    }

    fun pushFrontFirst(chat : Chat){
         this.messages = listOf(chat).plus(this.messages)
        notifyItemInserted(0)
    }

    fun pushFrontLast(chats : List<Chat>){
        this.messages = this.messages.plus(chats)
        //add(position, item);
        //notifyItemInserted(1);
    }
}