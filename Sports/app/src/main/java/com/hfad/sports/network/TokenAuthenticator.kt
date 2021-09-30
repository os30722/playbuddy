package com.hfad.sports.network


import com.hfad.sports.API_BASE_URL
import com.hfad.sports.api.AuthApi
import com.hfad.sports.util.TokenToolkit
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenAuthenticator : Authenticator {

    val refreshUrl = API_BASE_URL

    override fun authenticate(route: Route?, response: Response): Request? {

            val newToken = requestNewToken()
            val newRequest =
                response.request().newBuilder().header("Authorization", "Bearer $newToken")
                    .build()

            if (responseCount(response) >= 4) {

                TokenToolkit.clearToken()
                return null
            }
            return newRequest
        }


    fun requestNewToken(): String? {
         val refreshToken = TokenToolkit.getRefreshToken()

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $refreshToken")
                .build()
            chain.proceed(newRequest)
        }.build()
        val retrofit = Retrofit.Builder().baseUrl(refreshUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val call = retrofit.create(AuthApi::class.java).refreshToken()

        val response = call.execute().body()

        response?.refreshToken?.let { TokenToolkit.setRefreshToken(it) }
        response?.accessToken?.let { TokenToolkit.setAccessToken(it) }

        return response?.accessToken
    }

    private fun responseCount(response: Response): Int{
        var response: Response?  = response
        var i = 1
        while(response != null){
            i++
            response = response.priorResponse()
        }
        return i
    }

}