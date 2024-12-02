package com.cesoft.data.remote

import com.cesoft.data.entity.CountyDto
import com.cesoft.data.entity.ProductDto
import com.cesoft.data.entity.ProvinceDto
import com.cesoft.data.entity.StateDto
import com.cesoft.data.entity.StationDto
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {

    //@Headers("Accept: application/json") already in AuthInterceptor
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/{id}")
    suspend fun getByCounty(@Path("id") id: Int): Result<StationDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroCCAA/{id}")
    suspend fun getByState(@Path("id") id: Int): Result<StationDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvincia/{id}")
    suspend fun getByProvince(@Path("id") id: Int): Result<StationDto>

    /// Masters
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos")
    suspend fun getProducts(): Result<List<ProductDto>>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas/")
    suspend fun getStates(): Result<StateDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProvinciasPorComunidad/{id}")
    suspend fun getProvinceByState(@Path("id") id: Int): Result<ProvinceDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/{id}")
    suspend fun getCountyByProvince(@Path("id") id: Int): Result<CountyDto>
}