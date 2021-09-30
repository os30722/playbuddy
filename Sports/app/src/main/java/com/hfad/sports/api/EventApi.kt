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

interface EventApi {

    @POST("host")
    fun hostEvent(@Body event: Event): Flow<ApiResponse<ResponseHolder>>

    @GET("user/schedule")
    suspend fun userSchedule(
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ): EventList

    @GET("event/{eid}")
    fun eventInfo (
        @Path("eid") eventId: Int
    ) : Flow<ApiResponse<EventPage>>

    @GET("list/event")
    suspend fun recommendEvents(
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int,
        @Query("sport") event: String?
    ): EventList

    @GET("event/{eid}/joined")
    suspend fun getJoinedUsers(
        @Path("eid") eventId: Int,
        @Query("q") query: String?,
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ): UserList

    @GET("player/join/{eid}")
    fun joinGame(
        @Path("eid") eventId: Int
    ): Flow<ApiResponse<ResponseHolder>>

    @GET("event/requests/{eid}")
    suspend fun eventRequests(
        @Path("eid") eventId: Int,
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ) : UserList

    @GET("event/accept/{eid}")
    fun acceptRequest(
        @Path("eid") eventId: Int,
        @Query("uid") playerId: Int
    ): Call<Void>

    @GET("player/leave/{eid}")
    fun leaveGame(
        @Path("eid") eventId: Int
    ): Flow<ApiResponse<ResponseHolder>>


    @GET("event/remove/{eid}")
    fun removeFromGame(
        @Path("eid") eventId: Int,
        @Query("uid") playerId: Int
    ): Call<Void>

    @GET("remove/event")
    fun deleteEvent(
        @Query("eid") eventId: Int
    ): Flow<ApiResponse<ResponseHolder>>

    companion object {
        fun create(): EventApi {

            val BASE_URL = API_BASE_URL

            val client =OkHttpClient.Builder().addInterceptor(TokenInterceptor)
                .authenticator(TokenAuthenticator)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(FlowCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(EventApi::class.java)
        }


    }
}