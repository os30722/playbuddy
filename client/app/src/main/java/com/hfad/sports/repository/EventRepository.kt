package com.hfad.sports.repository

import android.util.Log
import androidx.paging.*
import com.hfad.sports.api.EventApi
import com.hfad.sports.data.PlayerJoinedSource
import com.hfad.sports.data.RecommendEventSource
import com.hfad.sports.data.ScheduleRemoteMediator
import com.hfad.sports.db.EventDatabase
import com.hfad.sports.network.networkBoundResource
import com.hfad.sports.data.JoinRequestSource
import com.hfad.sports.vo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class EventRepository(
    private val service: EventApi,
    private val database: EventDatabase
){
    
    suspend fun hostEvent(event: Event): Flow<Resource<ResponseHolder>> {
        return networkBoundResource(
            fetchFromRemote = { service.hostEvent(event)}
        ).flowOn(Dispatchers.IO)

    }

    @ExperimentalPagingApi
    fun getUserSchedule(): Flow<PagingData<EventPage>>{
        val pagingSourceFactory = {database.eventsDao().pagingSource()}

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = ScheduleRemoteMediator(
             database,
             service
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun getEventInfo(eventId: Int): Flow<Resource<EventPage>> {
        return networkBoundResource(
            fetchFromRemote = { service.eventInfo(eventId)}
        ).flowOn(Dispatchers.IO)

    }

    fun recommendEvents(event: String?): Flow<PagingData<EventPage>> {
        return Pager(
            config =PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecommendEventSource(event, service) }
        ).flow
    }

    fun getJoinedUsers(eventId: Int, query: String?): Flow<PagingData<UserPage>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PlayerJoinedSource(eventId, query, service) }
        ).flow
    }

    suspend fun joinGame(eventId: Int): Flow<Resource<ResponseHolder>> {
        return networkBoundResource(
            fetchFromRemote = { service.joinGame(eventId) }
        ).flowOn(Dispatchers.IO)
    }

    suspend fun leaveGame(eventId: Int): Flow<Resource<ResponseHolder>> {
        return networkBoundResource(
            fetchFromRemote = { service.leaveGame(eventId) }
        ).flowOn(Dispatchers.IO)
    }


    fun getJoinRequests(eventId: Int): Flow<PagingData<UserPage>> {
        return Pager(
            config =PagingConfig(
                pageSize = EventRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { JoinRequestSource(service, eventId) }
        ).flow
    }

    suspend fun deleteEvent(eventId: Int): Flow<Resource<ResponseHolder>> {
        return networkBoundResource(
            fetchFromRemote = { service.deleteEvent(eventId) }
        ).flowOn(Dispatchers.IO)
    }

    companion object{
        const val NETWORK_PAGE_SIZE = 10
    }

}