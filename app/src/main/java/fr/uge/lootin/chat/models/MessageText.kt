package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder

/**
 * [MessageText] represents a notification model
 */
data class MessageText(
        val text: String,
        val matchId: Long,
        val sender: Long
    ) {

    fun toJSON() : String =
        "{\"text\": \"$text\", \"matchId\": \"$matchId\", \"sender\": $sender}"


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
