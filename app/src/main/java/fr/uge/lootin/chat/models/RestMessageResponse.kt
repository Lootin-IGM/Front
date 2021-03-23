package fr.uge.lootin.chat.models

import java.time.LocalDateTime
import java.util.*

class MessagesResponse(val data : List<Message>) {



    class Message(val id : Long, val sendTime : String, val message : String, val sender: Long){



        override fun toString(): String {
            return "Message(sendTime=$sendTime, message='$message', sender=$sender)"
        }
    }

    override fun toString(): String {
        return "MessagesResponse(data=$data)"
    }
}