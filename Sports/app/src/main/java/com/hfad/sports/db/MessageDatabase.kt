package com.hfad.sports.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.Inbox
import com.hfad.sports.vo.InboxKeys

@Database(
    entities = [Conversation::class, Inbox::class, InboxKeys::class, InboxUpdate::class],
    version = 16,
    exportSchema = false
)

abstract class MessageDatabase: RoomDatabase(){

    abstract fun chatsDao(): ChatsDao
    abstract fun inboxDao(): InboxDao
    abstract fun inboxKeysDao(): InboxKeysDao

    companion object {

        @Volatile
        private var INSTANCE:MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                MessageDatabase::class.java, "Msg.db")
                .fallbackToDestructiveMigration()
                .build()
    }

}