package com.cesoft.data.remote

import com.cesoft.data.entity.CountyDto
import com.cesoft.data.entity.ProductDto
import com.cesoft.data.entity.ProvinceDto
import com.cesoft.data.entity.StateDto
import com.cesoft.data.entity.StationDto
import retrofit2.http.GET
import retrofit2.http.Path

// DOC : https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/help
interface ApiService {
    //@Headers("Accept: application/json") already in AuthInterceptor

    /// Stations
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroCCAA/{id}")
    suspend fun getByState(@Path("id") id: String): Result<StationDto>
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroCCAAProducto/{state}/{product}")
    suspend fun getByState(@Path("state") state: String, @Path("product") product: Int): Result<StationDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvincia/{id}")
    suspend fun getByProvince(@Path("id") id: String): Result<StationDto>
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProvinciaProducto/{province}/{product}")
    suspend fun getByProvince(@Path("province") province: String, @Path("product") product: Int): Result<StationDto>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/{id}")
    suspend fun getByCounty(@Path("id") id: Int): Result<StationDto>
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipioProducto/{county}/{product}")
    suspend fun getByCounty(@Path("county") county: Int, @Path("product") product: Int): Result<StationDto>

    /// Masters
    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos")
    suspend fun getProducts(): Result<List<ProductDto>>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ComunidadesAutonomas/")
    suspend fun getStates(): Result<List<StateDto>>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProvinciasPorComunidad/{id}")
    suspend fun getProvinceByState(@Path("id") id: String): Result<List<ProvinceDto>>

    @GET("/ServiciosRESTCarburantes/PreciosCarburantes/Listados/MunicipiosPorProvincia/{id}")
    suspend fun getCountyByProvince(@Path("id") id: Int): Result<List<CountyDto>>
}