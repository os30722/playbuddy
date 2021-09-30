package com.hfad.sports.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.sports.api.UserApi
import com.hfad.sports.repository.UserRepository
import com.hfad.sports.vo.UserPage
import retrofit2.HttpException
import java.io.IOException

class UserFriendPagingSource (
    private val userId: Int,
    private val query: String?,
    private val service: UserApi
): PagingSource<Int, UserPage>() {

    override fun getRefreshKey(state: PagingState<Int, UserPage>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPage> {
        try {
            var nextPageNumber: Int? = params.key ?: 1

            val users = service.getUserFriends(userId, query, nextPageNumber!!, params.loadSize)
            if(users.totalCount != 0){
                nextPageNumber += (params.loadSize / UserRepository.NETWORK_PAGE_SIZE)
            }

            else nextPageNumber = null

            return LoadResult.Page(
                data = users.items,
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