package com.cesoft.data.remote.result

import android.os.Build
import com.cesoft.domain.AppError
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
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
            if(error?.contains("User has no clocked sessions") == false) {
                android.util.Log.e("ApiExecutor", "handleApi---FAIL---------- ${response.code()} : $error")
            }
//            if(response.code() in 400..499 && error != null) {
//                val failure = Gson().fromJson(error, ErrorResponseDto::class.java)
//                val errorList = mutableListOf<String>()
//                try {
//                    val jsonObj = JSONObject(error.toString())
//                    val errors0 = JSONObject(jsonObj.get("errors").toString())
//                    val errors1 = JSONObject(errors0.get("errors").toString())
//                    for (key in errors1.keys()) {
//                        val errors2 = JSONArray(errors1.get(key).toString())
//                        for(i in 0..< errors2.length()) {
//                            android.util.Log.e("ApiExecutor", "handleApi---FAIL:ERRORS---------- $key = ${errors2.get(i)}")
//                            errorList.add(errors2.get(i).toString())
//                        }
//                    }
//                }
//                catch(_: Exception) {}
//                Result.failure(failure.toEntity(errorList))
//            }
//            else if(response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
//                Firebase.crashlytics.log("Server Error 404: $error / ${response.message()}")
//                Result.failure(NotFoundException())
//            }
            if(response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                android.util.Log.e("ApiExecutor","handleApi---HTTP_INTERNAL_ERROR---------- ${response.code()} : $error")
                Result.failure(AppError.InternalException)
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