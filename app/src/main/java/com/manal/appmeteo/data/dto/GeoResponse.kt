// GeoResponse.kt
package com.manal.appmeteo.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoResponse(
    val results: List<GeoResult>?
)

@JsonClass(generateAdapter = true)
data class GeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)