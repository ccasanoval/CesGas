package com.cesoft.domain.usecase

import com.cesoft.domain.FakeRepository
import com.cesoft.domain.FakeRepository.Call
import com.cesoft.domain.FakeRepository.Companion.station
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UseCasesTest {

    private val repository = FakeRepository()

    @Test
    fun `GetByStateUC passes product type only when present`() = runTest {
        GetByStateUC(repository)(10)
        GetByStateUC(repository)(10, ProductType.GLP)

        assertEquals(listOf(Call("state", 10), Call("state", 10, ProductType.GLP)), repository.calls)
    }

    @Test
    fun `GetByProvinceUC passes product type only when present`() = runTest {
        GetByProvinceUC(repository)(46)
        GetByProvinceUC(repository)(46, ProductType.GOA)

        assertEquals(listOf(Call("province", 46), Call("province", 46, ProductType.GOA)), repository.calls)
    }

    @Test
    fun `GetByCountyUC passes product type only when present`() = runTest {
        GetByCountyUC(repository)(7183)
        GetByCountyUC(repository)(7183, ProductType.G95)

        assertEquals(listOf(Call("county", 7183), Call("county", 7183, ProductType.G95)), repository.calls)
    }

    @Test
    fun `SetFilterUC and GetFilterUC round trip`() = runTest {
        val filter = Filter(productType = ProductType.GOA, state = 13, province = 28, zipCode = "28001")

        SetFilterUC(repository)(filter)

        assertEquals(filter, GetFilterUC(repository)().getOrNull())
    }

    @Test
    fun `SetCurrentStationUC and GetCurrentStationUC round trip`() = runTest {
        val station = station(42)

        SetCurrentStationUC(repository)(station)

        assertEquals(station, GetCurrentStationUC(repository)().getOrNull())
    }

    @Test
    fun `SetFavoritesUC and GetFavoritesUC round trip`() = runTest {
        val favorites = Favorites(products = setOf(ProductType.GOA), states = setOf(10, 13), counties = setOf(7183))

        SetFavoritesUC(repository)(favorites)

        assertEquals(favorites, GetFavoritesUC(repository)().getOrNull())
    }
}
