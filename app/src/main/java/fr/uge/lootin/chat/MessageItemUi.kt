package fr.uge.lootin.chat

import java.util.*

class MessageItemUi(val content:String,val textColor:Int,val messageType:Int, val id: Long, date : Date){
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
    }
}