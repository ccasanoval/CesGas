package com.cesoft.cesgas.ui

import app.cash.turbine.ReceiveTurbine
import com.cesoft.domain.entity.Location
import com.cesoft.domain.entity.Prices
import com.cesoft.domain.entity.Station

fun station(id: Int, location: Location = Location(39.6, -0.3)) = Station(
    id = id,
    zipCode = "46500",
    address = "Address $id",
    city = "Sagunto",
    county = "Sagunto/Sagunt",
    state = "Valencia",
    location = location,
    hours = "L-D: 24H",
    title = "Station $id",
    prices = Prices(G95 = 1.5f + id / 100f, G98 = null, GOA = null, GOB = null, GOC = null, GOAP = null, GLP = null),
)

/**
 * States hold an onEvent lambda that changes on every recomposition, so the same logical state
 * can be emitted several times: skip emissions until one of the expected type arrives.
 */
suspend inline fun <reified T> ReceiveTurbine<in T>.awaitState(): T {
    while (true) {
        val item = awaitItem()
        if (item is T) return item
    }
}
