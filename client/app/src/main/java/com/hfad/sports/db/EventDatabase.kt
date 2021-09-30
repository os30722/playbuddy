package com.hfad.sports.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.sports.databinding.Converters
import com.hfad.sports.vo.EventKeys
import com.hfad.sports.vo.EventPage


@Database(
    entities =[EventPage::class, EventKeys::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventsDao(): EventDao
    abstract fun eventKeysDao(): EventKeysDao

    companion object {

        @Volatile
        private var INSTANCE:EventDatabase? = null

        fun getInstance(context: Context): EventDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                EventDatabase::class.java, "Events.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}