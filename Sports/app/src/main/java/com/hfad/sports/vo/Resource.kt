package com.hfad.sports.vo

import com.hfad.sports.R
import retrofit2.HttpException
import java.lang.Exception

sealed class Resource<out R> {

    companion object {
        fun <T> success(data: T): Success<T> {
            return Success(data)
        }

        fun  error(e: Throwable): Resource<Nothing> {
            return Error(e)
        }

        fun inputError(msg: String): Resource<Nothing>{
            return InputError(msg)
        }

        fun httpError(message: String, statusCode: Int): Resource<Nothing>{
            return HttpError(message, statusCode)
        }

        fun loading(): Resource<Nothing> {
            return Loading
        }
    }
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: Throwable) : Resource<Nothing>()
    data class InputError(val message: String): Resource<Nothing>()
    data class HttpError(val message: String, val statusCode: Int): Resource<Nothing>()
    object Loading : Resource<Nothing>()

}





