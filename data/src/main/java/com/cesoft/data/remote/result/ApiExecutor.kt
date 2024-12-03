package com.cesoft.data.remote.result

import com.cesoft.domain.AppError
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

fun <T : Any> handleApi(
    execute: () -> Response<T>
): Result<T> {
    return try {
        val response = execute()
        val body = response.body()
        val error = response.errorBody()?.string()

        if(response.isSuccessful && body != null) {
            Result.success(body)
        }
        else {
            if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                android.util.Log.e("ApiExecutor","handleApi---HTTP_INTERNAL_ERROR---------- ${response.code()} : $error")
                Result.failure(AppError.InternalError(code = response.code(), msg = response.message()))
            }
            else {
                android.util.Log.e("ApiExecutor","handleApi---${response.code()}---------- ${response.code()} : $error")
                Result.failure(AppError.NetworkException(code = response.code(), msg = response.message()))
            }
        }
    } catch(e: HttpException) {
        android.util.Log.e("ApiExecutor","handleApi---HttpException---------- $e")
        Result.failure(AppError.NetworkException(code = e.code(), msg = e.message()))
    } catch(t: Throwable) {
        android.util.Log.e("ApiExecutor","handleApi---Throwable---------- $t")
        Result.failure(AppError.NetworkException(code = t.hashCode(), msg = t.message ?: ""))
    }
}