package com.hfad.sports.network.flowcalladapter

import android.util.Log
import com.hfad.sports.network.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import java.lang.reflect.Type
import kotlin.coroutines.resumeWithException


class FlowCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<ApiResponse<T>>> {

    override fun responseType() = responseType



    override fun adapt(call: Call<T>): Flow<ApiResponse<T>> = flow {

        val response = call.awaitResponse()
        emit(ApiResponse.create(response))

    }

}