package com.with_runn.mapData

import FacilityItem
import android.util.Log

class PetFacilityRepository {

    suspend fun fetchFacilites(
        apiKey: String
    ): List<FacilityItem>? {

//        // 원시 응답 확인용 로그
//        try {
//            val raw = MapRetrofitInstance.api.getFacilitiesRaw(
//                apiKey = apiKey,
//                page = 1,
//                perPage = 1000,
//                returnType = "JSON"
//            )
//            Log.e("RAW_BODY", raw.string())
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("API_ERROR", "Raw API call failed", e)
//        }

        // 실제 응답
        return try {
            val response = MapRetrofitInstance.api.getFacilities(
                apiKey = apiKey,
                page = 1,
                perPage = 1000,
                returnType = "JSON"
            )

            Log.d("API_DEBUG", "totalCount: ${response.totalCount}")
            Log.d("API_DEBUG", "currentCount: ${response.currentCount}")
            Log.d("API_DEBUG", "matchCount: ${response.matchCount}")
            Log.d("API_DEBUG", "data: ${response.data}")

            response.data
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("API_ERROR", "Exception during API call", e)
            null
        }
    }
}
