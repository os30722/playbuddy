package com.hfad.sports.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.sports.vo.EventPage


@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventPage>)

    @Query("select * from event order by DateTime(eventDate), DateTime(startTime)")
    fun pagingSource(): PagingSource<Int, EventPage>


    @Query("delete from event")
    suspend fun clearAll()

}