package com.hfad.sports.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.sports.api.MessageApi
import com.hfad.sports.db.MessageDatabase
import com.hfad.sports.vo.Conversation
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class ChatRemoteMediator (
    private val senderUId: Int,
    private val database: MessageDatabase,
    private val service: MessageApi,
): RemoteMediator<Int, Conversation>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Conversation>
    ): MediatorResult {
        val page: Int = when(loadType) {
            LoadType.REFRESH -> {
                0
            }
            LoadType.APPEND -> {
                database.chatsDao().getLastItem().id

            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        try {
            val apiResponse = service.getConversation(senderUId, page, state.config.pageSize)

            val conversations = apiResponse.items
            val endOfPaginationReached = conversations.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.chatsDao().clearAll()
                }

            }
            database.chatsDao().insertAll(conversations)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException){
            return MediatorResult.Error(e)
        } catch (e: HttpException){
            return MediatorResult.Error(e)
        }
    }


}