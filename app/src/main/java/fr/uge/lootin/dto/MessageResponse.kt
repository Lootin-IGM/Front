package fr.uge.lootin.dto

import java.util.*

class MessagesResponse(val data : List<Message>) {



    class Message(val id : Long, val sendTime : Date,val message : String, val sender: User){



        class User(val id: Long, val login: String){

            override fun toString(): String {
                return "Message(login=$login)"
            }
        }

        override fun toString(): String {
            return "Message(sendTime=$sendTime, message='$message', sender=$sender)"
        }
    }

    override fun toString(): String {
        return "MessagesResponse(data=$data)"
    }
}