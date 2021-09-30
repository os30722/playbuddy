package com.hfad.sports.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.sports.api.EventApi
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.EventPage
import retrofit2.HttpException
import java.io.IOException

class RecommendEventSource(
    private val event: String?,
    private val service: EventApi
) : PagingSource<Int, EventPage>() {

    override fun getRefreshKey(state: PagingState<Int, EventPage>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EventPage> {
        try {
            var nextPageNumber: Int? = params.key ?: 1
            val events = service.recommendEvents(nextPageNumber!!, params.loadSize, event)
            if(events.totalCount != 0){
                nextPageNumber += (params.loadSize / UserRepository.NETWORK_PAGE_SIZE)
            }

            else nextPageNumber = null

            return LoadResult.Page(
                data = events.items,
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: IOException){
            Log.d("debug64","Io + " + e.message.toString())
            return LoadResult.Error(e)
        } catch (e: HttpException){
            Log.d("debug64",e.message.toString())
            return LoadResult.Error(e)
        }

    }

}