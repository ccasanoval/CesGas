package com.cesoft.cesgas.ui

import android.content.Context
import com.cesoft.cesgas.R
import com.cesoft.domain.AppError
import java.io.IOException

//class Util(private var context: Context) {
//    fun errorString(error: AppError): String {
//        return error.message(context)
//    }
//}

fun Throwable.message(context: Context) = when(this) {
    is AppError.NotFound -> context.getString(R.string.error_not_found)
    is AppError.NoStateSelected -> context.getString(R.string.error_no_state_selected)
    is AppError.NoProductSelected -> context.getString(R.string.error_no_product_selected)
    // IOException: Retrofit failures (no connection, timeout...) arrive without being wrapped in AppError
    is AppError.NetworkException,
    is AppError.InternalError,
    is IOException -> context.getString(R.string.error_network)
    else -> context.getString(R.string.error_unknown)
}