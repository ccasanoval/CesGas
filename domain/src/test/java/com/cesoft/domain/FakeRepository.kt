package com.cesoft.domain

import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.Product
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract

/** Records every station query so tests can check which repository method a use case chose */
class FakeRepository(
    var stationsResult: Result<List<Station>> = Result.success(listOf()),
) : RepositoryContract {
    data class Call(val method: String, val id: Int, val productType: ProductType? = null)
    val calls = mutableListOf<Call>()

    var filter = Filter.Empty
    var currentStation = Station.Empty

    override suspend fun getFilter() = Result.success(filter)
    override suspend fun setFilter(filter: Filter): Result<Unit> {
        this.filter = filter
        return Result.success(Unit)
    }
    override suspend fun getCurrentStation() = Result.success(currentStation)
    override suspend fun setCurrentStation(station: Station): Result<Unit> {
        currentStation = station
        return Result.success(Unit)
    }

    override suspend fun getProducts() = Result.success(listOf<Product>())
    override suspend fun getStates() = Result.success(listOf<AddressState>())
    override suspend fun getProvinces(id: Int) = Result.success(listOf<AddressProvince>())
    override suspend fun getCounties(id: Int) = Result.success(listOf<AddressCounty>())

    override suspend fun getByState(id: Int) = record("state", id)
    override suspend fun getByState(id: Int, productType: ProductType) = record("state", id, productType)
    override suspend fun getByProvince(id: Int) = record("province", id)
    override suspend fun getByProvince(id: Int, productType: ProductType) = record("province", id, productType)
    override suspend fun getByCounty(id: Int) = record("county", id)
    override suspend fun getByCounty(id: Int, productType: ProductType) = record("county", id, productType)

    private fun record(method: String, id: Int, productType: ProductType? = null): Result<List<Station>> {
        calls.add(Call(method, id, productType))
        return stationsResult
    }

    companion object {
        fun station(id: Int, zipCode: String = "28001", g95: Float? = 1.5f) = Station(
            id = id,
            zipCode = zipCode,
            address = "Address $id",
            city = "City",
            county = "County",
            state = "State",
            location = Location(40.0, -3.0),
            hours = "L-D: 24H",
            title = "Station $id",
            prices = Prices(G95 = g95, G98 = null, GOA = null, GOB = null, GOC = null, GOAP = null, GLP = null),
        )
    }
}
