package com.hfad.sports.api

import com.hfad.sports.API_BASE_URL
import com.hfad.sports.network.TokenAuthenticator
import com.hfad.sports.network.TokenInterceptor
import com.hfad.sports.network.flowcalladapter.FlowCallAdapterFactory
import com.hfad.sports.vo.ConversationPage
import com.hfad.sports.vo.EventList
import com.hfad.sports.vo.InboxPage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {

    @GET("conversation/{rec}")
    suspend fun getConversation(
        @Path("rec") recipient: Int,
        @Query("crs") cursor: Int,
        @Query("pagesize") pageSize: Int
    ): ConversationPage

    @GET("inbox")
    suspend fun getInbox(
        @Query("crs") crs: Int,
        @Query("pagesize") pageSize: Int
    ): InboxPage


    companion object {
        fun create(): MessageApi {

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

            return retrofit.create(MessageApi::class.java)
        }


    }
}