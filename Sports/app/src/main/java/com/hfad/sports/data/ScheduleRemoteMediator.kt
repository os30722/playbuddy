package com.hfad.sports.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hfad.sports.api.EventApi
import com.hfad.sports.db.EventDatabase
import com.hfad.sports.vo.EventKeys
import com.hfad.sports.vo.EventPage
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class ScheduleRemoteMediator(
    private val database: EventDatabase,
    private val service: EventApi
): RemoteMediator<Int, EventPage>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventPage>
    ): MediatorResult {
        val page: Int = when(loadType){
            LoadType.REFRESH -> {
                1
            }
            LoadType.APPEND -> {
                database.eventKeysDao().getNextKey()
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val apiResponse = service.userSchedule(page, state.config.pageSize)

            val events = apiResponse.items
            val endOfPaginationReached = events.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.eventsDao().clearAll()
                }
                database.eventKeysDao().clearEventKeys()
            }
            val nextKey = if (endOfPaginationReached) null else page + 1

            if(events.lastOrNull() != null){
                database.eventKeysDao().insertAll(EventKeys(eventId = events.last().eventId, nextKey = nextKey))
            }

            database.eventsDao().insertAll(events)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException){
            return MediatorResult.Error(e)
        } catch (e: HttpException){
            return MediatorResult.Error(e)
        }

    }


}