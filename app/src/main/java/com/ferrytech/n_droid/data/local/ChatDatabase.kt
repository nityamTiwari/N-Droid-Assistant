package com.ferrytech.n_droid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatSessionEntity::class, MessageEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
