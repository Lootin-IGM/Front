package fr.uge.lootin.chat.models

import com.google.gson.GsonBuilder
import java.util.*

/**
 * [MessageTextResponse] represents a notification model
 */
data class MessagePictureResponse(
    val byte: ByteArray,
    val id_author: Long,
    val date: Date,
    val id: Long

    ) {

    fun toJSON() : String =
        "{\"byte\": \"$byte\", \"id_author\": \"$id_author\", \"date\": \"$date\", \"id\": \"$id\"}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessagePictureResponse

        if (!byte.contentEquals(other.byte)) return false
        if (id_author != other.id_author) return false
        if (date != other.date) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = byte.contentHashCode()
        result = 31 * result + id_author.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


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