package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessagePicture(
    val picture: String,
    val matchId: Long,
    val sender: Long

    ) {

    fun toJSON() : String =
        "{\"picture\": \"${picture.replace("\n","\\n" )}\", \"matchId\": $matchId, \"sender\": $sender}}"



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