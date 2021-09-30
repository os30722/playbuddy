package com.hfad.sports.api

import com.hfad.sports.API_BASE_URL
import com.hfad.sports.network.ApiResponse
import com.hfad.sports.network.flowcalladapter.FlowCallAdapterFactory
import com.hfad.sports.vo.LoggedUser
import com.hfad.sports.vo.Token
import com.hfad.sports.vo.UserLogin
import com.hfad.sports.vo.UserSignUp
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    fun loginUser(@Body userLogin: UserLogin): Flow<ApiResponse<Token>>

    @POST("signup")
    fun signUpUser(@Body useSignUp:  UserSignUp) : Flow<ApiResponse<Token>>

    @POST("refresh")
    fun refreshToken(): Call<Token>

    companion object {

        const val BASE_URL = API_BASE_URL

        fun create(): AuthApi {


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(FlowCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(AuthApi::class.java)
        }

    }
}