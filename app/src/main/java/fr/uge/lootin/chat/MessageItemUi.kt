package fr.uge.lootin.chat

class MessageItemUi(val content:String,val textColor:Int,val messageType:Int){
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
    }
}