package com.hfad.sports.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.sports.api.UserApi
import com.hfad.sports.vo.UserPage
import retrofit2.HttpException
import java.io.IOException

class SearchUserSource(
    private val query: String,
    private val service: UserApi
) : PagingSource<Int, UserPage>() {

    override fun getRefreshKey(state: PagingState<Int, UserPage>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPage> {
        try {
            var nextPageNumber: Int? = params.key ?: 1
            val result = service.searchUsers(query, nextPageNumber!!, params.loadSize)
            if(result.totalCount != 0)
                nextPageNumber = result.items[result.totalCount - 1].userId
            else nextPageNumber = null

            return LoadResult.Page(
                data = result.items,
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