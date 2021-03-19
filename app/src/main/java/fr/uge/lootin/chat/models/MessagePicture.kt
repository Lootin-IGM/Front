package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessagePicture(
    val byte: ByteArray,
    val id_author: Long,
    val date: Date,

    ) {

    fun toJSON() : String =
        "{\"byte\": \"$byte\", \"id_author\": \"$id_author\", \"date\": \"$date\"}"


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