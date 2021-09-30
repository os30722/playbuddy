package com.hfad.sports.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.sports.vo.EventKeys


@Dao
interface EventKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: EventKeys)

    @Query("select * from event_keys where eventId = :eventId")
    suspend fun eventKeysId(eventId: Int): EventKeys?

    @Query("delete from event_keys")
    suspend fun clearEventKeys()

    @Query("select nextKey from event_keys limit 1")
    suspend fun getNextKey():Int


}