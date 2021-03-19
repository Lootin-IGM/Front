package fr.uge.lootin.chat.adapter

import android.graphics.Bitmap
import android.graphics.Color
import java.util.*

class MessageItemUi private constructor(
    val content: String? = null,
    val picture: Bitmap? = null,
    val textColor:Int,
    val messageType:Int,
    val id: Long,
    val date: Date){


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageItemUi

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {

        fun factoryMessageItemUI( content:String, id: Long, date : Date, iAuthor : Boolean): MessageItemUi {
            if( iAuthor)return MessageItemUi(content,null, MY_COLOR, TYPE_MY_MESSAGE, id, date)
            return MessageItemUi(content, null, FRIEND_COLOR, TYPE_FRIEND_MESSAGE, id, date)
        }

        fun factoryPictureItemUI( bitmap: Bitmap, id: Long, date : Date, iAuthor : Boolean): MessageItemUi {
            if( iAuthor)return MessageItemUi(null,bitmap, MY_COLOR, TYPE_MY_MESSAGE_PICTURE, id, date)
            return MessageItemUi(null, bitmap, FRIEND_COLOR, TYPE_FRIEND_MESSAGE_PICTURE, id, date)
        }
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
        const val TYPE_MY_MESSAGE_PICTURE = 2
        const val TYPE_FRIEND_MESSAGE_PICTURE = 3

        private const val MY_COLOR = Color.WHITE
        private const val FRIEND_COLOR = Color.DKGRAY
    }
}