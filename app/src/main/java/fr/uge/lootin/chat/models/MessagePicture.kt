package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessagePicture(
    val picture: String,
    val matchID: Long,
    val sendTo: Long,
    val sender: Long

    ) {

    fun toJSON() : String =
        "{\"picture\": \"${picture.replace("\n","\\n" )}\", \"matchID\": $matchID, \"sendTo\": $sendTo, \"sender\": $sender}}"



    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageTextResponse] object
         */
        fun fromApi(payload: String): MessagePicture =
            GsonBuilder()
                .create()
                .fromJson(payload, MessagePicture::class.java)
    }

}