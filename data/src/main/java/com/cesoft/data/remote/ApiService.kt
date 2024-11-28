package com.cesoft.data.remote

import com.cesoft.data.entity.ByCityDto
import com.cesoft.data.entity.ProductDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {

    //@Headers("Accept: application/json") already in AuthInterceptor
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos")
    suspend fun getProducts(): Result<List<ProductDto>>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/{id}")
    suspend fun getByCity(@Path("id") id: Int): Result<ByCityDto>
}