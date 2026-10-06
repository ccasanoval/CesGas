package com.cesoft.domain.usecase

import com.cesoft.domain.AppError
import com.cesoft.domain.FakeRepository
import com.cesoft.domain.FakeRepository.Call
import com.cesoft.domain.FakeRepository.Companion.station
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class FilterStationsUCTest {

    private lateinit var repository: FakeRepository
    private lateinit var filterStations: FilterStationsUC

    @Before
    fun setUp() {
        repository = FakeRepository()
        filterStations = FilterStationsUC(
            GetByStateUC(repository),
            GetByProvinceUC(repository),
            GetByCountyUC(repository),
        )
    }

    @Test
    fun `county filter has priority over province and state`() = runTest {
        filterStations(Filter(productType = ProductType.GOA, state = 10, province = 46, county = 7183))

        assertEquals(listOf(Call("county", 7183, ProductType.GOA)), repository.calls)
    }

    @Test
    fun `province filter is used when there is no county`() = runTest {
        filterStations(Filter(productType = ProductType.G95, state = 10, province = 46))

        assertEquals(listOf(Call("province", 46, ProductType.G95)), repository.calls)
    }

    @Test
    fun `state filter is used when there is no province`() = runTest {
        filterStations(Filter(productType = ProductType.G98, state = 13))

        assertEquals(listOf(Call("state", 13, ProductType.G98)), repository.calls)
    }

    @Test
    fun `county without province falls back to state`() = runTest {
        filterStations(Filter(state = 13, county = 4354))

        assertEquals(listOf(Call("state", 13)), repository.calls)
    }

    @Test
    fun `no state returns empty list without calling the repository`() = runTest {
        val result = filterStations(Filter(productType = ProductType.G95, province = 46, county = 7183))

        assertEquals(listOf<Station>(), result.getOrThrow())
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun `null product type calls the overload without product`() = runTest {
        filterStations(Filter(state = 10))

        assertEquals(listOf(Call("state", 10, null)), repository.calls)
    }

    @Test
    fun `zip code keeps only matching stations`() = runTest {
        repository.stationsResult = Result.success(listOf(
            station(1, zipCode = "46500"),
            station(2, zipCode = "46520"),
            station(3, zipCode = "46520"),
        ))

        val result = filterStations(Filter(state = 10, zipCode = "46520"))

        assertEquals(listOf(2, 3), result.getOrThrow().map { it.id })
    }

    @Test
    fun `blank zip code does not filter`() = runTest {
        repository.stationsResult = Result.success(listOf(station(1, "46500"), station(2, "46520")))

        val result = filterStations(Filter(state = 10, zipCode = "  "))

        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun `repository failure is propagated instead of returning an empty list`() = runTest {
        val error = AppError.NetworkException(500, "boom")
        repository.stationsResult = Result.failure(error)

        val result = filterStations(Filter(state = 10))

        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `repository failure is propagated when filtering by zip code`() = runTest {
        val error = IOException("no connection")
        repository.stationsResult = Result.failure(error)

        val result = filterStations(Filter(state = 10, zipCode = "46520"))

        assertSame(error, result.exceptionOrNull())
    }
}
