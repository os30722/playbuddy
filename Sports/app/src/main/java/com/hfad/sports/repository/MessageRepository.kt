package com.hfad.sports.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.*
import com.hfad.sports.api.MessageApi
import com.hfad.sports.data.ChatRemoteMediator
import com.hfad.sports.data.InboxRemoteMediator
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.vo.Conversation
import com.hfad.sports.vo.Inbox
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val service: MessageApi,
    private val database: MessageDatabase
) {

    @ExperimentalPagingApi
    fun getConversations(senderId: Int, recipientId: Int): Flow<PagingData<Conversation>> {
        val pagingSourceFactory = {database.chatsDao().pagingSource(senderId, recipientId)}

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = ChatRemoteMediator(
                recipientId,
                database,
                service
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    @ExperimentalPagingApi
    fun getInbox(): Flow<PagingData<Inbox>>{
        val pagingSourceFactory = {database.inboxDao().pagingSource()}

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = InboxRemoteMediator(
                database,
                service
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}