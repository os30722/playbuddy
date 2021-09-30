package com.hfad.sports.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.sports.api.EventApi
import com.hfad.sports.repository.EventRepository
import com.hfad.sports.vo.UserPage
import retrofit2.HttpException
import java.io.IOException

class JoinRequestSource   (
    private val service: EventApi,
    private val eventId: Int
) : PagingSource<Int, UserPage>() {

    override fun getRefreshKey(state: PagingState<Int, UserPage>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPage> {
        try {
            var nextPageNumber: Int? = params.key ?: 1
            val events = service.eventRequests(eventId, nextPageNumber!!, params.loadSize)
            if(events.totalCount != 0){
                nextPageNumber += (params.loadSize / EventRepository.NETWORK_PAGE_SIZE)
            }

            else nextPageNumber = null

            return LoadResult.Page(
                data = events.items,
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: IOException){
            return LoadResult.Error(e)
        } catch (e: HttpException){
            return LoadResult.Error(e)
        }

    }

}