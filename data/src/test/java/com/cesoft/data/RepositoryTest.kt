package com.cesoft.data

import com.cesoft.data.entity.ProvinceDto
import com.cesoft.data.entity.StateDto
import com.cesoft.data.entity.StationDataDto
import com.cesoft.data.entity.StationDto
import com.cesoft.data.prefs.PrefDataSource
import com.cesoft.data.remote.RemoteDataSource
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.ProductType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryTest {

    private val prefs = mockk<PrefDataSource>(relaxed = true)
    private val remote = mockk<RemoteDataSource>()
    private val repository = Repository(prefs, remote)

    private fun station(id: String, g95: String = "", goa: String = "") = StationDataDto(
        zipCode = "28001", address = "", hours = "", latitude = "40,4", longitude = "-3,7",
        city = "", county = "", goA = goa, goB = "", goC = "", goAP = "",
        g95e10 = "", g95e5 = g95, g95e5P = "", g98e10 = "", g98e5 = "", glp = "",
        state = "", title = "Station $id", idStation = id, idCity = "", idProvince = "", idState = "",
    )

    private val stations = StationDto(listOf(
        station("1", g95 = "1,700", goa = "1,500"),
        station("2", g95 = "1,500"),
        station("3", goa = "1,400"),
        station("4", g95 = "1,600", goa = "1,450"),
    ))

    @Test
    fun `state and province ids are zero padded for the API`() = runTest {
        coEvery { remote.getByState(any()) } returns Result.success(StationDto(listOf()))
        coEvery { remote.getByProvince(any()) } returns Result.success(StationDto(listOf()))
        coEvery { remote.getProvinces(any()) } returns Result.success(listOf())

        repository.getByState(1)
        repository.getByState(13, ProductType.G95)
        repository.getByProvince(2)
        repository.getByProvince(46, ProductType.GOA)
        repository.getProvinces(7)

        coVerify { remote.getByState("01") }
        coVerify { remote.getByState("13") }
        coVerify { remote.getByProvince("02") }
        coVerify { remote.getByProvince("46") }
        coVerify { remote.getProvinces("07") }
    }

    @Test
    fun `filtering by product drops stations without that price and sorts by it`() = runTest {
        coEvery { remote.getByState("10") } returns Result.success(stations)

        val g95 = repository.getByState(10, ProductType.G95).getOrThrow()
        val goa = repository.getByState(10, ProductType.GOA).getOrThrow()

        assertEquals(listOf(2, 4, 1), g95.map { it.id })
        assertEquals(listOf(3, 4, 1), goa.map { it.id })
    }

    @Test
    fun `product ALL keeps every station`() = runTest {
        coEvery { remote.getByCounty(7183) } returns Result.success(stations)

        val all = repository.getByCounty(7183, ProductType.ALL).getOrThrow()

        assertEquals(listOf(1, 2, 3, 4), all.map { it.id })
    }

    @Test
    fun `query without product keeps every station in API order`() = runTest {
        coEvery { remote.getByProvince("46") } returns Result.success(stations)

        val all = repository.getByProvince(46).getOrThrow()

        assertEquals(listOf(1, 2, 3, 4), all.map { it.id })
    }

    @Test
    fun `remote failure is propagated`() = runTest {
        val error = AppError.NetworkException(503, "Service Unavailable")
        coEvery { remote.getByState(any()) } returns Result.failure(error)

        val result = repository.getByState(10, ProductType.G95)

        assertTrue(result.isFailure)
        assertSame(error, result.exceptionOrNull())
    }

    @Test
    fun `masters are mapped to domain entities`() = runTest {
        coEvery { remote.getStates() } returns Result.success(listOf(StateDto(10, "Comunitat Valenciana")))
        coEvery { remote.getProvinces("10") } returns Result.success(listOf(ProvinceDto(46, "Valencia")))

        assertEquals(listOf(AddressState(10, "Comunitat Valenciana")), repository.getStates().getOrThrow())
        assertEquals(listOf(AddressProvince(46, "Valencia")), repository.getProvinces(10).getOrThrow())
    }
}
