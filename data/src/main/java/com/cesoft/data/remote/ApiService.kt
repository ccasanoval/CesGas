package com.cesoft.data.remote

import com.cesoft.data.entity.ProductDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    //@Headers("Accept: application/json") already in AuthInterceptor
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos")
    suspend fun getProducts(): Result<List<ProductDto>>


}