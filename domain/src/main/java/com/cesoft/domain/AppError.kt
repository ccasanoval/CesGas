package com.cesoft.domain

sealed class AppError: Throwable() {

    class NoProductSelected: AppError()
    class NoStateSelected: AppError()

    class UnknownError: AppError()
    class NotFound: AppError()

    data class InternalError(val code: Int = 0, val msg: String = ""): AppError()
    data class NetworkException(val code: Int = 0, val msg: String = ""): AppError()

    data class DataBaseError(val e: Throwable): AppError()
    data class FileError(val filename: String): AppError()

    companion object {
//        fun fromThrowable(e: Throwable): AppError = when(e) {
//            //is NetworkException -> NetworkError
//            else -> UnknownError(message = e.localizedMessage ?: "?")
//        }
    }
}