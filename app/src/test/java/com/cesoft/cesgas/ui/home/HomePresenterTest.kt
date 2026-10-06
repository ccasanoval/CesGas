package com.cesoft.cesgas.ui.home

import com.cesoft.cesgas.ui.awaitState
import com.cesoft.cesgas.ui.common.FilterField
import com.cesoft.cesgas.ui.common.FilterOptions
import com.cesoft.cesgas.ui.map.MapScreen
import com.cesoft.cesgas.ui.station
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.AddressCounty
import com.cesoft.domain.entity.AddressProvince
import com.cesoft.domain.entity.AddressState
import com.cesoft.domain.entity.Favorites
import com.cesoft.domain.entity.Filter
import com.cesoft.domain.entity.ProductType
import com.cesoft.domain.entity.Station
import com.cesoft.domain.repository.RepositoryContract
import com.cesoft.domain.usecase.FilterStationsUC
import com.cesoft.domain.usecase.GetByCountyUC
import com.cesoft.domain.usecase.GetByProvinceUC
import com.cesoft.domain.usecase.GetByStateUC
import com.cesoft.domain.usecase.GetCountiesUC
import com.cesoft.domain.usecase.GetFavoritesUC
import com.cesoft.domain.usecase.GetFilterUC
import com.cesoft.domain.usecase.GetProvincesUC
import com.cesoft.domain.usecase.GetStatesUC
import com.cesoft.domain.usecase.SetCurrentStationUC
import com.cesoft.domain.usecase.SetFavoritesUC
import com.cesoft.domain.usecase.SetFilterUC
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class HomePresenterTest {

    private val repository = mockk<RepositoryContract>()
    private val navigator = FakeNavigator(HomeScreen)
    private val stations = listOf(station(1), station(2))
    private val states = listOf(AddressState(10, "Comunitat Valenciana"), AddressState(13, "Madrid"))
    private val provinces = listOf(AddressProvince(46, "Valencia"))
    private val counties = listOf(AddressCounty(7183, "Sagunto"))

    /** The filter is stored as the real prefs would: what is set is what is read next */
    private var storedFilter = Filter(productType = ProductType.G95, state = 10)
    private var storedFavorites = Favorites.Empty

    @Before
    fun setUp() {
        val filterSlot = slot<Filter>()
        coEvery { repository.getFilter() } answers { Result.success(storedFilter) }
        coEvery { repository.setFilter(capture(filterSlot)) } answers {
            storedFilter = filterSlot.captured
            Result.success(Unit)
        }
        val favoritesSlot = slot<Favorites>()
        coEvery { repository.getFavorites() } answers { Result.success(storedFavorites) }
        coEvery { repository.setFavorites(capture(favoritesSlot)) } answers {
            storedFavorites = favoritesSlot.captured
            Result.success(Unit)
        }
        coEvery { repository.setCurrentStation(any()) } returns Result.success(Unit)
        coEvery { repository.getStates() } returns Result.success(states)
        coEvery { repository.getProvinces(any()) } returns Result.success(provinces)
        coEvery { repository.getCounties(any()) } returns Result.success(counties)
        coEvery { repository.getByState(any(), any()) } returns Result.success(stations)
        coEvery { repository.getByProvince(any(), any()) } returns Result.success(stations)
        coEvery { repository.getByCounty(any(), any()) } returns Result.success(stations)
    }

    private fun presenter() = HomePresenter(
        navigator = navigator,
        getFilter = GetFilterUC(repository),
        setFilter = SetFilterUC(repository),
        getFavorites = GetFavoritesUC(repository),
        setFavorites = SetFavoritesUC(repository),
        filterStations = FilterStationsUC(
            GetByStateUC(repository), GetByProvinceUC(repository), GetByCountyUC(repository)
        ),
        setCurrentStation = SetCurrentStationUC(repository),
        getStates = GetStatesUC(repository),
        getProvinces = GetProvincesUC(repository),
        getCounties = GetCountiesUC(repository),
    )

    private fun options(vararg selectedIds: Int) =
        FilterOptions(selectedIds.map { FilterField(it, "Field $it", selected = true) })

    @Test
    fun `saved favorites are part of the state`() = runTest {
        storedFavorites = Favorites(products = setOf(ProductType.GOA), states = setOf(13), provinces = setOf(46))

        presenter().test {
            assertEquals(storedFavorites, awaitState<HomeState.Success>().favorites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favorites are shown even when loading the stations fails`() = runTest {
        storedFavorites = Favorites(states = setOf(13))
        coEvery { repository.getByState(any(), any()) } returns Result.failure(IOException())

        presenter().test {
            assertEquals(storedFavorites, awaitState<HomeState.Success>().favorites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applying a filter saves the starred options and keeps favorites not shown`() = runTest {
        // Madrid (28) is in another state, so it is not among the provinces shown now
        storedFavorites = Favorites(provinces = setOf(28, 46))
        val shown = FilterOptions(listOf(
            FilterField(46, "Valencia", selected = true, favorite = false),// unstarred
            FilterField(3, "Alicante"),
            FilterField(12, "Castellón", favorite = true),// starred
        ))

        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeAddressProvince(shown))

            awaitState<HomeState.Loading>()
            assertEquals(setOf(28, 12), awaitState<HomeState.Success>().favorites.provinces)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `starring a product saves it`() = runTest {
        val shown = FilterOptions(listOf(
            FilterField(ProductType.G95.ordinal, "G95", selected = true),
            FilterField(ProductType.GLP.ordinal, "GLP", favorite = true),
        ))

        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeProduct(shown))

            awaitState<HomeState.Loading>()
            awaitState<HomeState.Success>()
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(setOf(ProductType.GLP), storedFavorites.products)
    }

    @Test
    fun `starts loading and then shows the stations of the saved filter`() = runTest {
        presenter().test {
            assertTrue(awaitItem() is HomeState.Loading)

            val state = awaitState<HomeState.Success>()
            assertEquals(stations, state.stations)
            assertEquals(storedFilter, state.filter)
            assertEquals(HomePresenter.PRODUCTS, state.masters.products)
            assertEquals(states, state.masters.states)
            assertEquals(provinces, state.masters.provinces)
            assertEquals(listOf<AddressCounty>(), state.masters.counties)
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repository.getByState(10, ProductType.G95) }
    }

    @Test
    fun `loads counties when there is a province in the filter`() = runTest {
        storedFilter = Filter(productType = ProductType.GOA, state = 10, province = 46)

        presenter().test {
            val state = awaitState<HomeState.Success>()
            assertEquals(counties, state.masters.counties)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repository.getCounties(46) }
        coVerify { repository.getByProvince(46, ProductType.GOA) }
    }

    @Test
    fun `network failure is shown as error and not as an empty result`() = runTest {
        val error = IOException("no connection")
        coEvery { repository.getByState(any(), any()) } returns Result.failure(error)

        presenter().test {
            val state = awaitState<HomeState.Success>()
            assertSame(error, state.error)
            assertTrue(state.stations.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `failure loading states is shown as error`() = runTest {
        val error = AppError.NetworkException(503, "Service Unavailable")
        coEvery { repository.getStates() } returns Result.failure(error)

        presenter().test {
            assertSame(error, awaitState<HomeState.Success>().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `no stations found shows NotFound`() = runTest {
        coEvery { repository.getByState(any(), any()) } returns Result.success(listOf())

        presenter().test {
            assertTrue(awaitState<HomeState.Success>().error is AppError.NotFound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter without state asks to select one`() = runTest {
        storedFilter = Filter(productType = ProductType.G95)

        presenter().test {
            val state = awaitState<HomeState.Success>()
            assertTrue(state.error is AppError.NoStateSelected)
            assertEquals(states, state.masters.states)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter without product asks to select one but still loads provinces`() = runTest {
        storedFilter = Filter(state = 10)

        presenter().test {
            val state = awaitState<HomeState.Success>()
            assertTrue(state.error is AppError.NoProductSelected)
            assertEquals(provinces, state.masters.provinces)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing the product saves the filter and reloads with it`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeProduct(options(ProductType.GOA.ordinal)))

            awaitState<HomeState.Loading>()
            val state = awaitState<HomeState.Success>()
            assertEquals(ProductType.GOA, state.filter.productType)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(ProductType.GOA, storedFilter.productType)
        coVerify { repository.getByState(10, ProductType.GOA) }
    }

    @Test
    fun `deselecting the product asks to select one`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeProduct(FilterOptions(listOf())))

            awaitState<HomeState.Loading>()
            assertTrue(awaitState<HomeState.Success>().error is AppError.NoProductSelected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing the state clears province and county`() = runTest {
        storedFilter = Filter(productType = ProductType.G95, state = 10, province = 46, county = 7183)

        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeAddressState(options(13)))

            awaitState<HomeState.Loading>()
            awaitState<HomeState.Success>()
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(Filter(productType = ProductType.G95, state = 13), storedFilter)
        coVerify { repository.getByState(13, ProductType.G95) }
    }

    @Test
    fun `changing the province clears the county`() = runTest {
        storedFilter = Filter(productType = ProductType.G95, state = 10, province = 46, county = 7183)

        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeAddressProvince(options(12)))

            awaitState<HomeState.Loading>()
            awaitState<HomeState.Success>()
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(Filter(productType = ProductType.G95, state = 10, province = 12), storedFilter)
    }

    @Test
    fun `changing the zip code keeps the rest of the filter`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.ChangeAddressZipCode("46500"))

            awaitState<HomeState.Loading>()
            awaitState<HomeState.Success>()
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(Filter(productType = ProductType.G95, state = 10, zipCode = "46500"), storedFilter)
    }

    @Test
    fun `Load intent reloads the data`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.Load)

            awaitState<HomeState.Loading>()
            awaitState<HomeState.Success>()
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 2) { repository.getByState(10, ProductType.G95) }
    }

    @Test
    fun `going to a station saves it and opens the map`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.GoMap(stations[1]))

            assertEquals(MapScreen, navigator.awaitNextScreen())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repository.setCurrentStation(stations[1]) }
    }

    @Test
    fun `going to the map without station clears the current one`() = runTest {
        presenter().test {
            awaitState<HomeState.Success>().onEvent(HomeIntent.GoMap())

            assertEquals(MapScreen, navigator.awaitNextScreen())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { repository.setCurrentStation(Station.Empty) }
    }
}
