package com.cesoft.data.remote.result

import android.util.Log
import com.cesoft.domain.AppError
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

private const val TAG = "ApiExecutor"

fun <T : Any> handleApi(
    execute: () -> Response<T>
): Result<T> {
    return try {
        val response = execute()
        val body = response.body()

        if(response.isSuccessful && body != null) {
            Result.success(body)
        }
        else {
            Log.w(TAG, "HTTP ${response.code()} ${response.message()}: ${response.errorBody()?.string()}")
            if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                Result.failure(AppError.InternalError(code = response.code(), msg = response.message()))
            }
            else {
                Result.failure(AppError.NetworkException(code = response.code(), msg = response.message()))
            }
        }
    } catch(e: HttpException) {
        Log.w(TAG, "HTTP ${e.code()}", e)
        Result.failure(AppError.NetworkException(code = e.code(), msg = e.message()))
    } catch(t: Throwable) {
        Log.w(TAG, "Request failed", t)
        Result.failure(AppError.NetworkException(msg = t.message ?: ""))
    }
}
