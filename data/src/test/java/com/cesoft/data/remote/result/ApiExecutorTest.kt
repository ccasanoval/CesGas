package com.cesoft.data.remote.result

import com.cesoft.domain.AppError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ApiExecutorTest {

    private fun errorBody() = "{}".toResponseBody("application/json".toMediaType())

    @Test
    fun `successful response returns the body`() {
        val result = handleApi { Response.success(listOf("a", "b")) }

        assertEquals(listOf("a", "b"), result.getOrThrow())
    }

    @Test
    fun `HTTP 500 maps to InternalError`() {
        val result = handleApi<String> { Response.error(500, errorBody()) }

        val error = result.exceptionOrNull()
        assertTrue(error is AppError.InternalError)
        assertEquals(500, (error as AppError.InternalError).code)
    }

    @Test
    fun `other HTTP errors map to NetworkException`() {
        val result = handleApi<String> { Response.error(404, errorBody()) }

        val error = result.exceptionOrNull()
        assertTrue(error is AppError.NetworkException)
        assertEquals(404, (error as AppError.NetworkException).code)
    }

    @Test
    fun `successful response without body is a failure`() {
        val result = handleApi<String> { Response.success(null) }

        assertTrue(result.exceptionOrNull() is AppError.NetworkException)
    }

    @Test
    fun `thrown exception maps to NetworkException with its message`() {
        val result = handleApi<String> { throw IOException("timeout") }

        val error = result.exceptionOrNull()
        assertTrue(error is AppError.NetworkException)
        assertEquals("timeout", (error as AppError.NetworkException).msg)
    }
}
