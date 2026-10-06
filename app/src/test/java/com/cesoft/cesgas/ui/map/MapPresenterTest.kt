package com.cesoft.cesgas.ui.map

import com.cesoft.cesgas.ui.awaitState
import com.cesoft.cesgas.ui.home.HomeScreen
import com.cesoft.cesgas.ui.station
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCurrentStationUC
import com.cesoft.domain.usecase.GetFilterUC
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MapPresenterTest {

    private val repository = mockk<RepositoryContract>()
    private val navigator = FakeNavigator(HomeScreen, MapScreen)
    private val filter = Filter(productType = ProductType.G95, state = 10)
    private val stations = listOf(station(1), station(2))

    @Before
    fun setUp() {
        coEvery { repository.getFilter() } returns Result.success(filter)
        coEvery { repository.getCurrentStation() } returns Result.success(Station.Empty)
        coEvery { repository.getByState(any(), any()) } returns Result.success(stations)
    }

    private fun presenter() = MapPresenter(
        navigator = navigator,
        getCurrentStation = GetCurrentStationUC(repository),
        getFilter = GetFilterUC(repository),
        filterStations = FilterStationsUC(
            GetByStateUC(repository), GetByProvinceUC(repository), GetByCountyUC(repository)
        ),
    )

    @Test
    fun `shows only the selected station with the saved filter`() = runTest {
        val selected = station(7)
        coEvery { repository.getCurrentStation() } returns Result.success(selected)

        presenter().test {
            assertTrue(awaitItem() is MapState.Loading)

            val state = awaitState<MapState.Success>()
            assertEquals(listOf(selected), state.stations)
            assertEquals(filter, state.filter)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { repository.getByState(any(), any()) }
    }

    @Test
    fun `without selected station shows the stations of the filter`() = runTest {
        presenter().test {
            val state = awaitState<MapState.Success>()
            assertEquals(stations, state.stations)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selected station without location counts as no selection`() = runTest {
        coEvery { repository.getCurrentStation() } returns Result.success(station(7, Location(0.0, 0.0)))

        presenter().test {
            assertEquals(stations, awaitState<MapState.Success>().stations)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `network failure is shown as error`() = runTest {
        val error = IOException("no connection")
        coEvery { repository.getByState(any(), any()) } returns Result.failure(error)

        presenter().test {
            val state = awaitState<MapState.Success>()
            assertSame(error, state.error)
            assertTrue(state.stations.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no stations found shows NotFound`() = runTest {
        coEvery { repository.getByState(any(), any()) } returns Result.success(listOf())

        presenter().test {
            assertTrue(awaitState<MapState.Success>().error is AppError.NotFound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Close goes back`() = runTest {
        presenter().test {
            awaitState<MapState.Success>().onEvent(MapIntent.Close)

            navigator.awaitPop()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
