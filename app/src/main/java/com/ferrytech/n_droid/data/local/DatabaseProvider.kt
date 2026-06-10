package com.ferrytech.n_droid.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    lateinit var database: ChatDatabase
        private set

    fun init(context: Context) {
        if (!this::database.isInitialized) {
            database = Room.databaseBuilder(
                context.applicationContext,
                ChatDatabase::class.java,
                "n_droid_chat_db"
            ).build()
        }
    }
}
