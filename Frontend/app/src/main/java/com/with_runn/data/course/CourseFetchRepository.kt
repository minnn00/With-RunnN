package com.with_runn.data.course

import com.with_runn.data.TokenManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CourseFetchRepository (private val api: CourseApi){

    // --- caches ---
    @Volatile private var risingCached: List<CourseSummary>? = null
    private val risingMutex = Mutex()

    private data class NearbyKey(val provinceId: Int, val cityId: Int?, val townId: Int?)
    private val nearbyCache = mutableMapOf<NearbyKey, List<CourseSummary>>()
    private val nearbyMutex = Mutex()

    // --- helpers ---
    private fun bearer(): String = "Bearer " + TokenManager.getAccessToken().orEmpty()

    suspend fun getCourseDetail(courseId: Int, accessToken: String): CourseDetailResponse{
        return api.getCourseDetail("Bearer $accessToken", courseId)
    }


    // 1) /api/course/rising  (cached)
    suspend fun getRisingCourses(): List<CourseSummary> {
        risingCached?.let { return it } // 캐시 존재시 반환

        return risingMutex.withLock {
            risingCached ?: api.getRisingCourses(bearer()).also { risingCached = it }
        }
    }

    // 2) /api/course/rising/search (no cache)
    suspend fun searchRisingCourses(keyword: String? = null): List<CourseSummary> {
        return api.searchRisingCourses(bearer(), keyword)
    }

    // 3) /api/course/nearby  (cached per region key)
    suspend fun getNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ): List<CourseSummary> {
        val key = NearbyKey(provinceId, cityId, townId)

        nearbyCache[key]?.let { return it }

        return nearbyMutex.withLock {
            nearbyCache[key] ?: api.getNearbyCourses(bearer(), provinceId, cityId, townId)
                .also { nearbyCache[key] = it }
        }
    }

    // 4) /api/course/nearby/search (no cache)
    suspend fun searchNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String? = null
    ): List<CourseSummary> {
        return api.searchNearbyCourses(bearer(), provinceId, cityId, townId, keyword)
    }

    fun clearCourseCaches() {
        risingCached = null
        nearbyCache.clear()
    }
}