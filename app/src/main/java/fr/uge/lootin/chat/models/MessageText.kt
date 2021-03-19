package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageText] represents a notification model
 */
data class MessageText(
    val content: String,
    val id_author: Long,
    ) {

    fun toJSON() : String =
        "{\"content\": \"$content\", \"id_author\": \"$id_author\"}"


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
