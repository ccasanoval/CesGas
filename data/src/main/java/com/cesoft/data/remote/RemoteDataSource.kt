package com.cesoft.data.remote

import android.content.Context
import com.cesoft.data.BuildConfig
import com.cesoft.data.entity.CountyDto
import com.cesoft.data.entity.ProductDto
import com.cesoft.data.entity.ProvinceDto
import com.cesoft.data.entity.StateDto
import com.cesoft.data.entity.StationDto
import com.cesoft.data.remote.result.NetworkResultCallAdapterFactory
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

class RemoteDataSource(
    private val context: Context,
    api: ApiService? = null,
) {
    private val apiService: ApiService

    init {
        apiService = api ?: getRetrofit(API).create(ApiService::class.java)
        android.util.Log.e("RemoteDS", "API:--------------------------------------- $API")
    }

    class GasInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            return chain.proceed(request.build())
        }
    }

    private fun getHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(GasInterceptor())
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
        }
        httpClient.cache(Cache(context.cacheDir, 1024 * 1024 * 2L))
        httpClient.connectTimeout(Duration.ofSeconds(30))
        httpClient.readTimeout(Duration.ofSeconds(30))
        return httpClient.build()
    }

    private fun getRetrofit(url: String): Retrofit {
        val gson = GsonBuilder()
            //.setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
            .client(getHttpClient())
            .build()
    }

    //----------------------------------------------------------------------------------------------
    suspend fun getProducts(): Result<List<ProductDto>> {
        return apiService.getProducts()
    }
    suspend fun getStates(): Result<List<StateDto>> {
        return apiService.getStates()
    }
    suspend fun getProvinces(id: String): Result<List<ProvinceDto>> {
        return apiService.getProvinceByState(id)
    }
    suspend fun getCounties(id: Int): Result<List<CountyDto>> {
        return apiService.getCountyByProvince(id)
    }

    suspend fun getByState(id: String): Result<StationDto> {
        return apiService.getByState(id)
    }
    suspend fun getByState(id: String, product: Int): Result<StationDto> {
        return apiService.getByState(id, product)
    }
    suspend fun getByProvince(id: String): Result<StationDto> {
        return apiService.getByProvince(id)
    }
    suspend fun getByProvince(id: String, product: Int): Result<StationDto> {
        return apiService.getByProvince(id, product)
    }
    suspend fun getByCounty(id: Int): Result<StationDto> {
        return apiService.getByCounty(id)
    }
    suspend fun getByCounty(id: Int, product: Int): Result<StationDto> {
        return apiService.getByCounty(id, product)
    }

    //----------------------------------------------------------------------------------------------
    companion object {
        const val API = BuildConfig.API_URL//"https://api.sergest.desarrollo.systems"
    }
}