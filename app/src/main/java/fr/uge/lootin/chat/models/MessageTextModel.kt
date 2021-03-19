package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextModel] represents a notification model
 */
data class MessageTextModel(
    val content: String,
    val id_author: Long,
    val date: Date,

    ) {

    fun toJSON() : String =
        "{\"content\": \"$content\", \"id_author\": \"$id_author\", \"date\": \"$date\"}"


    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageTextModel] object
         */
        fun fromApi(payload: String): MessageTextModel =
            GsonBuilder()
                .create()
                .fromJson(payload, MessageTextModel::class.java)
    }

}
