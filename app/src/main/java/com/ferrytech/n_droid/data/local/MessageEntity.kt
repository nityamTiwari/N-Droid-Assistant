package com.ferrytech.n_droid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferrytech.n_droid.data.model.Message

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val imageUri: String?,
    val chatMode: String
)

fun MessageEntity.toMessage(): Message {
    return Message(
        id = id,
        text = text,
        isUser = isUser,
        timestamp = timestamp,
        imageUri = imageUri
    )
}

fun Message.toEntity(sessionId: String, chatMode: String): MessageEntity {
    return MessageEntity(
        id = id,
        sessionId = sessionId,
        text = text,
        isUser = isUser,
        timestamp = timestamp,
        imageUri = imageUri,
        chatMode = chatMode
    )
}
