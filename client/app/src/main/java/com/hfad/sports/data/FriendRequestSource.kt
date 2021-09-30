package com.hfad.sports.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.sports.api.UserApi
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.UserPage
import retrofit2.HttpException
import java.io.IOException

class FriendRequestSource   (
    private val service: UserApi
) : PagingSource<Int, UserPage>() {

    override fun getRefreshKey(state: PagingState<Int, UserPage>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPage> {
        try {
            var nextPageNumber: Int? = params.key ?: 1
            val events = service.getFriendRequests(nextPageNumber!!, params.loadSize)
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
            return LoadResult.Error(e)
        } catch (e: HttpException){
            return LoadResult.Error(e)
        }

    }

}