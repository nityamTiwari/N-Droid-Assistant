package com.ferrytech.n_droid.data.model

data class Message(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)