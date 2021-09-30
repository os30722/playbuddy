package com.hfad.sports.network

import com.hfad.sports.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

suspend inline fun <RequestType> networkBoundResource(
    crossinline fetchFromRemote :() -> Flow<ApiResponse<RequestType>>
) = flow<Resource<RequestType>> {

    emit(Resource.loading())
    try {
        val response = fetchFromRemote()
        response.collect { apiResponse ->
            when (apiResponse) {
                is ApiSuccess -> apiResponse.body?.let {
                    emit(Resource.success(it))
                }
                is ApiError -> {
                    emit(Resource.httpError(apiResponse.errorMsg, apiResponse.statusCode))
                }
            }
        }

    }catch (e: Exception){
        emit(Resource.error(e))
    }

}