package com.hfad.sports.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.sports.api.MessageApi
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.vo.Inbox
import com.hfad.sports.vo.InboxKeys
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class InboxRemoteMediator(
    private val database: MessageDatabase,
    private val service: MessageApi
) : RemoteMediator<Int, Inbox>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Inbox>
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                1
            }

            LoadType.APPEND -> {
                database.inboxKeysDao().getNextKey()
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)

            }
        }
        try {
            val apiResponse = service.getInbox(page, state.config.pageSize)

            val inbox = apiResponse.items
            val endOfPaginationReached = inbox.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.inboxDao().clearAll()
                }
                database.inboxKeysDao().clearInboxKeys()
            }
            val nextKey = if (endOfPaginationReached) null else page + 1

            if (inbox.lastOrNull() != null) {
                database.inboxKeysDao()
                    .insertAll(InboxKeys(uid = inbox.last().user.userId, nextKey = nextKey))
            }

            database.inboxDao().insertAll(inbox)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException){
            return MediatorResult.Error(e)
        } catch(e: HttpException){
            return MediatorResult.Error(e)
        }
    }
}