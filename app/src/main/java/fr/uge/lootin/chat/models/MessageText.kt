package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageText] represents a notification model
 */
data class MessageText(
    val text: String,
    val matchId: Long,
    ) {

    fun toJSON() : String =
        "{\"text\": \"$text\", \"matchId\": \"$matchId\"}"


    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageText] object
         */
        fun fromApi(payload: String): MessageText =
            GsonBuilder()
                .create()
                .fromJson(payload, MessageText::class.java)
    }

}
