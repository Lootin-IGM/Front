package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder

/**
 * [MessageModel] represents a notification model
 */
data class MessageModel(
    val content: String,


    ) {

    fun toJSON() : String =
        "{\"name\": \"$content\"}"


    companion object {
        /**
         * Creates a Notification object from api
         * @param payload - the api json result
         * @return a [MessageModel] object
         */
        fun fromApi(payload: String): MessageModel =
            GsonBuilder()
                .create()
                .fromJson(payload, MessageModel::class.java)
    }

}
