package com.hfad.sports.api

import com.hfad.sports.API_BASE_URL
import com.hfad.sports.network.ApiResponse
import com.hfad.sports.network.TokenAuthenticator
import com.hfad.sports.network.TokenInterceptor
import com.hfad.sports.network.flowcalladapter.FlowCallAdapterFactory
import com.hfad.sports.vo.*
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UserApi {

    @GET("search/user")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ): UserList

    @GET("user/{uid}/friends")
    suspend fun getUserFriends(
        @Path("uid") userId: Int,
        @Query("q") query: String?,
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ): UserList

    @GET("user/profile/{uid}")
    fun getProfile(
        @Path("uid") userId: Int
    ) : Flow<ApiResponse<UserProfile>>


    @GET("friend/request/{uid}")
    fun friendRequest(
        @Path("uid") userId: Int
    ) : Flow<ApiResponse<ResponseHolder>>


    @POST("register")
    fun registerDevice(@Body loggedUser: LoggedUser): Call<Void>

    @GET("friends")
    suspend fun getFriendRequests(
        @Query("crs") cursor: Int,
        @Query("pagesize") pageSize: Int
    ): UserList

    @GET("friend/accept/{uid}")
    fun acceptFriend(
        @Path("uid") userId: Int
    ): Call<Void>

    @GET("friend/remove/{uid}")
    fun removeFriend (
        @Path("uid") userId: Int
    ) : Call<Void>

    @GET("user/{uid}")
    suspend fun getUserPage(
        @Path("uid") userId: Int
    ): UserPage

    companion object {
        fun create(): UserApi {

            val BASE_URL = API_BASE_URL

            val client = OkHttpClient.Builder().addInterceptor(TokenInterceptor)
                .authenticator(TokenAuthenticator)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(FlowCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(UserApi::class.java)
        }
    }
}