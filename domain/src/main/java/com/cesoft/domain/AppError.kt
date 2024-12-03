package com.cesoft.domain

sealed class AppError: Throwable() {

    data object NoStateSelected: AppError() {
        private fun readResolve(): Any = NoStateSelected
    }

    data object UnknownError: AppError() {
        private fun readResolve(): Any = UnknownError
    }

    data class InternalError(val code: Int = 0, val msg: String = ""): AppError()
    data class NetworkException(val code: Int = 0, val msg: String = ""): AppError()

    data object NotFound: AppError() {
        private fun readResolve(): Any = NotFound
    }

    data class DataBaseError(val e: Throwable): AppError() {
        private fun readResolve(): Any = DataBaseError(Throwable())
    }

    data class FileError(val filename: String): AppError()

    companion object {
//        fun fromThrowable(e: Throwable): AppError = when(e) {
//            //is NetworkException -> NetworkError
//            else -> UnknownError(message = e.localizedMessage ?: "?")
//        }
    }
}