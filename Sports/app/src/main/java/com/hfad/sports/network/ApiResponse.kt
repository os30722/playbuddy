package com.hfad.sports.network

import retrofit2.Response

sealed class ApiResponse<T>() {

    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if(response.isSuccessful){
                val body = response.body()
                response
                if(body == null || response.code() == 204){
                    ApiEmptyResponse()
                }else{
                    ApiSuccess(
                        body = body,
                        responseCode = response.code()
                    )
                }
            }else{
                val msg = response.errorBody()?.string()
                val errorMsg = if(msg.isNullOrEmpty()){
                    response.message()
                } else{
                    msg
                }
                ApiError(errorMsg = errorMsg, statusCode = response.code())
            }

        }

        fun <T> create(err: Throwable): ApiResponse<T> {
            return ApiError(
                errorMsg = err.message ?: "unknown error",
                statusCode = 0
            )
        }

    }

}

class ApiEmptyResponse<T> : ApiResponse<T>()
data class ApiSuccess<T>(val body: T?, val responseCode :Int? = null) : ApiResponse<T>()
data class ApiError<T>(val errorMsg: String, val statusCode: Int) : ApiResponse<T>()