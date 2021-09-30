package com.hfad.sports.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.EventPage

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversation: List<Conversation>)

    @Query("select * from conversations where senderId in(:senderId, :recipientId) and recipientId in(:senderId,:recipientId)order by id desc")
    fun pagingSource(senderId: Int, recipientId: Int): PagingSource<Int, Conversation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConversation(conversation: Conversation)

    @Query("select * from conversations order by id asc limit 1")
    suspend fun getLastItem(): Conversation

    @Query("delete from conversations")
    suspend fun clearAll()
}