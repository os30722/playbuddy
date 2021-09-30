package com.hfad.sports.network

import android.util.Log
import com.hfad.sports.util.TokenToolkit
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object TokenInterceptor : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenToolkit.getAccessToken()
        val request = requestWithToken(chain.request(), token)
        return chain.proceed(request)
    }

    private fun requestWithToken(req: Request, token: String): Request{
        return req.newBuilder()
            .addHeader("Authorization", "Bearer " + token)
            .build()
    }

}