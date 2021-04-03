package fr.uge.lootin.chat_manager.preview_message

import android.graphics.Bitmap


enum class TypeMessage {
    TEXT, PHOTO
}
class PreviewMessage(val id_match: Int, val message: String, val sender: String, val id_sender: Int, val photo: Bitmap, val sendTime: String, val type: TypeMessage) {
}
