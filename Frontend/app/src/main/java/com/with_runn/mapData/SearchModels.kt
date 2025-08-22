package com.with_runn.mapData

import com.google.gson.annotations.SerializedName

data class MapSearchItem(
    val id: Long,
    val name: String,
    val category: String?,
    @SerializedName("longitude") val latitude: Double,
    @SerializedName("latitude") val longitude: Double,
    val address: String?,
    @SerializedName("closed_day")   val closedDay: String?,
    @SerializedName("running_time") val runningTime: String?,
    @SerializedName("has_parking")  val hasParking: String?
)

data class MapSearchResponse(
    val code: String,
    val message: String,
    val result: MapSearchResult,
    val success: Boolean
)

data class MapSearchResult(
    val content: List<MapSearchItem>,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean
)
