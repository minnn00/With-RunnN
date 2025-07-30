package com.with_runn.mapData

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    val status: String,
    val routes: List<Route>,
    val errorMessage: String? = null
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline
)

data class OverviewPolyline(
    val points: String
)