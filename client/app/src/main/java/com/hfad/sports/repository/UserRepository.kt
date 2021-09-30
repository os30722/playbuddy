package com.hfad.sports.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hfad.sports.api.UserApi
import com.hfad.sports.api.AuthApi
import com.hfad.sports.data.FriendRequestSource
import com.hfad.sports.data.SearchUserSource
import com.hfad.sports.data.UserFriendPagingSource
import com.hfad.sports.network.networkBoundResource
import com.hfad.sports.vo.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class UserRepository
    constructor(
        private val authService: AuthApi,
        private val userService: UserApi
) {

    suspend fun loginUser(userLogin: UserLogin): Flow<Resource<Token>>{
        return networkBoundResource(
            fetchFromRemote = { authService.loginUser(userLogin)}
        ).flowOn(Dispatchers.IO)
    }

    suspend fun signUpUser(userSignUp: UserSignUp): Flow<Resource<Token>>{
        return networkBoundResource (
            fetchFromRemote = { authService.signUpUser(userSignUp)}
        ).flowOn(Dispatchers.IO)
    }

    fun searchUsers(query: String): Flow<PagingData<UserPage>> {
        return Pager(
            config =PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchUserSource(query, userService) }
        ).flow
    }

    suspend fun getProfile(userId: Int): Flow<Resource<UserProfile>>{
        return networkBoundResource (
            fetchFromRemote = { userService.getProfile(userId) }
        ).flowOn(Dispatchers.IO)
    }

    suspend fun friendRequest(userId: Int): Flow<Resource<ResponseHolder>> {
        return networkBoundResource (
            fetchFromRemote = { userService.friendRequest(userId) }
        ).flowOn(Dispatchers.IO)
    }

    fun getFriendRequests(): Flow<PagingData<UserPage>> {
        return Pager(
            config =PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FriendRequestSource(userService) }
        ).flow
    }

    fun getUserFriends(userId: Int, query: String?): Flow<PagingData<UserPage>> {
        return Pager (
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserFriendPagingSource(userId, query, userService) }
         ). flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10

    }

}