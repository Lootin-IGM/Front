package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessageTextResponse(
    val message: String,
    val id_author: Long,
    val sendTime: Date,
    val id: Long


    ) {

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
