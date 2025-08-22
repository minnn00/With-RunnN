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

    // 1) /api/course/rising  — 항상 네트워크에서 새로 가져와 캐시 갱신
    suspend fun getRisingCourses(): List<CourseSummary> {
        return risingMutex.withLock {
            val fresh = api.getRisingCourses(bearer())
            risingCached = fresh
            fresh
        }
    }

    // 2) /api/course/rising/search (no cache)
    suspend fun searchRisingCourses(keyword: String? = null): List<CourseSummary> {
        return api.searchRisingCourses(bearer(), keyword)
    }

    // 3) /api/course/nearby — 항상 네트워크에서 새로 가져와 캐시 갱신
    suspend fun getNearbyCourses(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ): List<CourseSummary> {
        val key = NearbyKey(provinceId, cityId, townId)
        return nearbyMutex.withLock {
            val fresh = api.getNearbyCourses(bearer(), provinceId, cityId, townId)
            nearbyCache[key] = fresh
            fresh
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
