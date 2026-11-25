package com.cesoft.data

import android.os.SystemClock
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
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps in memory what was downloaded: masters (states, provinces, counties) for the whole session,
 * as they don't change, and the stations of the last areas for [STATIONS_TTL_MS], so the map and
 * product changes reuse the list Home already has. Failures and empty answers are never cached,
 * as an empty list may be a temporary server problem.
 */
class Repository(
    private val prefs: PrefDataSource,
    //private val local: LocalDataSource,
    private val remote: RemoteDataSource,
    // Monotonic: moving the phone's clock back must not keep old prices longer
    private val clock: () -> Long = SystemClock::elapsedRealtime,
): RepositoryContract {

    @Volatile private var states: List<AddressState>? = null
    private val provinces = ConcurrentHashMap<Int, List<AddressProvince>>()
    private val counties = ConcurrentHashMap<Int, List<AddressCounty>>()

    private class CachedStations(val time: Long, val stations: List<Station>)
    /** Least recently used first: the oldest area is dropped when there are too many (a state can be MBs) */
    private val stations = object : LinkedHashMap<String, CachedStations>(MAX_CACHED_AREAS, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CachedStations>?) =
            size > MAX_CACHED_AREAS
    }

    /// PREFS
    override suspend fun getFilter(): Result<Filter> = Result.success(prefs.getFilter())
    override suspend fun setFilter(filter: Filter): Result<Unit> = Result.success(prefs.setFilter(filter))

    /// FAVORITES
    override suspend fun getFavorites(): Result<Favorites> = Result.success(prefs.getFavorites())
    override suspend fun setFavorites(favorites: Favorites): Result<Unit> =
        Result.success(prefs.setFavorites(favorites))

    /// CACHE
    override fun clearCache() {
        states = null
        provinces.clear()
        counties.clear()
        synchronized(stations) { stations.clear() }
    }

    /// REMOTE MASTERS
    override suspend fun getProducts(): Result<List<Product>> =
        remote.getProducts().map { list -> list.map { it.toEntity() } }
    override suspend fun getStates(): Result<List<AddressState>> {
        states?.let { return Result.success(it) }
        return remote.getStates().map { list -> list.map { it.toEntity() } }
            .onSuccess { if(it.isNotEmpty()) states = it }
    }
    override suspend fun getProvinces(id: Int): Result<List<AddressProvince>> =
        provinces.cached(id) { remote.getProvinces(id.toApiId()).map { list -> list.map { it.toEntity() } } }
    override suspend fun getCounties(id: Int): Result<List<AddressCounty>> =
        counties.cached(id) { remote.getCounties(id).map { list -> list.map { it.toEntity() } } }

    /// REMOTE STATIONS
    override suspend fun getByState(id: Int): Result<List<Station>> =
        cachedStations("state:$id") { remote.getByState(id.toApiId()).toStations() }
    override suspend fun getByState(id: Int, productType: ProductType): Result<List<Station>> =
        getByState(id).filterByType(productType)

    override suspend fun getByProvince(id: Int): Result<List<Station>> =
        cachedStations("province:$id") { remote.getByProvince(id.toApiId()).toStations() }
    override suspend fun getByProvince(id: Int, productType: ProductType): Result<List<Station>> =
        getByProvince(id).filterByType(productType)

    override suspend fun getByCounty(id: Int): Result<List<Station>> =
        cachedStations("county:$id") { remote.getByCounty(id).toStations() }
    override suspend fun getByCounty(id: Int, productType: ProductType): Result<List<Station>> =
        getByCounty(id).filterByType(productType)

    private suspend fun <V : Collection<*>> MutableMap<Int, V>.cached(id: Int, fetch: suspend () -> Result<V>): Result<V> {
        get(id)?.let { return Result.success(it) }
        return fetch().onSuccess { if(it.isNotEmpty()) put(id, it) }
    }

    private suspend fun cachedStations(
        key: String,
        fetch: suspend () -> Result<List<Station>>
    ): Result<List<Station>> {
        val now = clock()
        synchronized(stations) { stations[key] }
            ?.takeIf { now - it.time < STATIONS_TTL_MS }
            ?.let { return Result.success(it.stations) }
        return fetch().onSuccess { list ->
            if(list.isNotEmpty()) synchronized(stations) { stations[key] = CachedStations(now, list) }
        }
    }

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

    companion object {
        /** Prices are updated about every half hour */
        const val STATIONS_TTL_MS = 10 * 60 * 1000L
        private const val MAX_CACHED_AREAS = 4
    }
}
