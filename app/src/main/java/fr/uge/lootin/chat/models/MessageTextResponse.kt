package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessageTextResponse(
    val content: String,
    val id_author: Long,
    val date: Date,
    val id: Long

    ) {

    fun toJSON() : String =
        "{\"content\": \"$content\", \"id_author\": \"$id_author\", \"date\": \"$date\", \"id\": \"$id\"}"


    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageTextResponse] object
         */
        fun fromApi(payload: String): MessageTextResponse =
            GsonBuilder()
                .create()
                .fromJson(payload, MessageTextResponse::class.java)
    }

}
