package com.cesoft.domain.entity

import android.location.Location

data class Station(
    val cp: String,
    val address: String,
    val hours: String,
    val location: Location,
    val margin: String,

) {
}