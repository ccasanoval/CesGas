package com.cesoft.data

import com.cesoft.data.entity.StationDto
import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val prefs: PrefDataSource,
    //private val local: LocalDataSource,
    private val remote: RemoteDataSource
): RepositoryContract {

    /// PREFS
    override suspend fun getFilter(): Result<Filter> = Result.success(prefs.getFilter())
    override suspend fun setFilter(filter: Filter): Result<Unit> = Result.success(prefs.setFilter(filter))

    /// FAVORITES
    override suspend fun getFavorites(): Result<Favorites> = Result.success(prefs.getFavorites())
    override suspend fun setFavorites(favorites: Favorites): Result<Unit> =
        Result.success(prefs.setFavorites(favorites))

    /// CURRENT STATION
    override suspend fun getCurrentStation(): Result<Station> = Result.success(prefs.getCurrentStation())
    override suspend fun setCurrentStation(station: Station): Result<Unit> =
        Result.success(prefs.setCurrentStation(station))

    /// REMOTE MASTERS
    override suspend fun getProducts(): Result<List<Product>> =
        remote.getProducts().map { list -> list.map { it.toEntity() } }
    override suspend fun getStates(): Result<List<AddressState>> =
        remote.getStates().map { list -> list.map { it.toEntity() } }
    override suspend fun getProvinces(id: Int): Result<List<AddressProvince>> =
        remote.getProvinces(id.toApiId()).map { list -> list.map { it.toEntity() } }
    override suspend fun getCounties(id: Int): Result<List<AddressCounty>> =
        remote.getCounties(id).map { list -> list.map { it.toEntity() } }

    /// REMOTE STATIONS
    override suspend fun getByState(id: Int): Result<List<Station>> =
        remote.getByState(id.toApiId()).toStations()
    override suspend fun getByState(id: Int, productType: ProductType): Result<List<Station>> =
        getByState(id).filterByType(productType)

    override suspend fun getByProvince(id: Int): Result<List<Station>> =
        remote.getByProvince(id.toApiId()).toStations()
    override suspend fun getByProvince(id: Int, productType: ProductType): Result<List<Station>> =
        getByProvince(id).filterByType(productType)

    override suspend fun getByCounty(id: Int): Result<List<Station>> =
        remote.getByCounty(id).toStations()
    override suspend fun getByCounty(id: Int, productType: ProductType): Result<List<Station>> =
        getByCounty(id).filterByType(productType)

    /** The API wants state and province ids with two digits: "1" is not found, "01" is */
    private fun Int.toApiId() = toString().padStart(2, '0')

    /** A state can have thousands of stations: map them off the main thread */
    private suspend fun Result<StationDto>.toStations(): Result<List<Station>> =
        withContext(Dispatchers.Default) {
            // list can be null despite its type: Gson doesn't know about Kotlin nullability
            map { dto -> dto.list.orEmpty().map { it.toEntity() } }
        }

    /** Only stations that sell the product, cheapest first. ALL and UNKNOWN keep every station */
    private suspend fun Result<List<Station>>.filterByType(productType: ProductType): Result<List<Station>> {
        if(productType == ProductType.ALL || productType == ProductType.UNKNOWN) return this
        return withContext(Dispatchers.Default) {
            map { list ->
                list.filter { it.prices.of(productType) != null }
                    .sortedBy { it.prices.of(productType) }
            }
        }
    }
}
