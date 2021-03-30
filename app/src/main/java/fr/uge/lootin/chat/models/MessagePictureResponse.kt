package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessagePictureResponse(
        val id: Long,
        val matchId: Long,
    ) {

    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageTextResponse] object
         */
        fun fromApi(payload: String): MessagePictureResponse =
            GsonBuilder()
                .create()
                .fromJson(payload, MessagePictureResponse::class.java)
    }
}