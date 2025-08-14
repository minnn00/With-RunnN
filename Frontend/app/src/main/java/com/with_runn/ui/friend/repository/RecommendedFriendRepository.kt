package com.with_runn.ui.friend.repository

import android.util.Log
import com.with_runn.ui.friend.model.dto.RecommendedFriendResponse
import com.with_runn.ui.friend.model.dto.FriendDetailResponse
import com.with_runn.ui.friend.model.dto.BlockUserResponse
import com.with_runn.ui.friend.model.dto.ReportRequest
import com.with_runn.ui.friend.network.FriendRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecommendedFriendRepository {
    private val apiService = FriendRetrofitClient.friendApiService

    suspend fun getRecommendedFriends(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ): Result<List<RecommendedFriendResponse>> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "API 호출 시작: provinceId=$provinceId, cityId=$cityId, townId=$townId")
            
            val response = apiService.getRecommendedFriends(provinceId, cityId, townId)
            
            Log.d("RecommendedFriendRepository", "API 응답 코드: ${response.code()}")
            
            if (response.isSuccessful) {
                val friends = response.body() ?: emptyList()
                Log.d("RecommendedFriendRepository", "추천 친구 수: ${friends.size}")
                if (friends.isNotEmpty()) {
                    friends.forEach { friend ->
                        Log.d("RecommendedFriendRepository", "친구: ${friend.userName} (ID: ${friend.userId})")
                    }
                } else {
                    Log.d("RecommendedFriendRepository", "추천 친구가 없습니다.")
                }
                Result.success(friends)
            } else {
                Log.e("RecommendedFriendRepository", "API 호출 실패: ${response.code()}")
                Result.failure(Exception("API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    suspend fun getAllFriends(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null
    ): Result<List<RecommendedFriendResponse>> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "모든 친구 API 호출 시작: provinceId=$provinceId, cityId=$cityId, townId=$townId")
            
            val response = apiService.getAllFriends(provinceId, cityId, townId)
            
            Log.d("RecommendedFriendRepository", "모든 친구 API 응답 코드: ${response.code()}")
            
            if (response.isSuccessful) {
                val friends = response.body() ?: emptyList()
                Log.d("RecommendedFriendRepository", "모든 친구 수: ${friends.size}")
                if (friends.isNotEmpty()) {
                    friends.forEach { friend ->
                        Log.d("RecommendedFriendRepository", "친구: ${friend.userName} (ID: ${friend.userId})")
                    }
                } else {
                    Log.d("RecommendedFriendRepository", "모든 친구가 없습니다.")
                }
                Result.success(friends)
            } else {
                Log.e("RecommendedFriendRepository", "모든 친구 API 호출 실패: ${response.code()}")
                Result.failure(Exception("모든 친구 API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "모든 친구 API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    suspend fun getFriendDetail(userId: Int): Result<FriendDetailResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "친구 상세 정보 API 호출 시작: userId=$userId")
            
            val response = apiService.getFriendDetail(userId)
            
            Log.d("RecommendedFriendRepository", "친구 상세 정보 API 응답 코드: ${response.code()}")
            
            if (response.isSuccessful) {
                val friendDetail = response.body()
                if (friendDetail != null) {
                    Log.d("RecommendedFriendRepository", "친구 상세 정보 조회 성공: ${friendDetail.name} (ID: ${friendDetail.userId})")
                    Result.success(friendDetail)
                } else {
                    Log.e("RecommendedFriendRepository", "친구 상세 정보가 null입니다.")
                    Result.failure(Exception("친구 상세 정보가 null입니다."))
                }
            } else {
                Log.e("RecommendedFriendRepository", "친구 상세 정보 API 호출 실패: ${response.code()}")
                Result.failure(Exception("친구 상세 정보 API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "친구 상세 정보 API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    suspend fun searchFriends(
        provinceId: Int,
        cityId: Int? = null,
        townId: Int? = null,
        keyword: String
    ): Result<List<RecommendedFriendResponse>> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "친구 검색 API 호출 시작: provinceId=$provinceId, cityId=$cityId, townId=$townId, keyword=$keyword")
            
            val response = apiService.searchFriends(provinceId, cityId, townId, keyword)
            
            Log.d("RecommendedFriendRepository", "친구 검색 API 응답 코드: ${response.code()}")
            
            if (response.isSuccessful) {
                val friends = response.body() ?: emptyList()
                Log.d("RecommendedFriendRepository", "검색 결과 친구 수: ${friends.size}")
                if (friends.isNotEmpty()) {
                    friends.forEach { friend ->
                        Log.d("RecommendedFriendRepository", "검색된 친구: ${friend.userName} (ID: ${friend.userId})")
                    }
                } else {
                    Log.d("RecommendedFriendRepository", "검색 결과가 없습니다.")
                }
                Result.success(friends)
            } else {
                Log.e("RecommendedFriendRepository", "친구 검색 API 호출 실패: ${response.code()}")
                Result.failure(Exception("친구 검색 API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "친구 검색 API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    suspend fun followFriend(userId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "친구 팔로우 API 호출 시작: userId=$userId")
            
            val response = apiService.followFriend(userId)
            
            Log.d("RecommendedFriendRepository", "친구 팔로우 API 응답 코드: ${response.code()}")
            
            when (response.code()) {
                200 -> {
                    val result = response.body() ?: "팔로우 완료 (userId=$userId)"
                    Log.d("RecommendedFriendRepository", "팔로우 성공: $result")
                    Result.success(result)
                }
                500 -> {
                    Log.e("RecommendedFriendRepository", "서버 내부 오류 (500) - 서버 측 문제")
                    Result.failure(Exception("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
                }
                else -> {
                    Log.e("RecommendedFriendRepository", "친구 팔로우 API 호출 실패: ${response.code()}")
                    Result.failure(Exception("팔로우에 실패했습니다. (오류 코드: ${response.code()})"))
                }
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "친구 팔로우 API 호출 중 예외 발생", e)
            
            // EOFException (응답 본문 읽기 실패) 처리
            when (e) {
                is java.io.EOFException -> {
                    Log.e("RecommendedFriendRepository", "응답 본문 읽기 실패 - 서버 응답 문제")
                    Result.failure(Exception("서버 응답을 읽을 수 없습니다. 잠시 후 다시 시도해주세요."))
                }
                is java.net.SocketTimeoutException -> {
                    Log.e("RecommendedFriendRepository", "네트워크 타임아웃")
                    Result.failure(Exception("네트워크 연결이 지연되고 있습니다. 잠시 후 다시 시도해주세요."))
                }
                is java.net.UnknownHostException -> {
                    Log.e("RecommendedFriendRepository", "서버 연결 실패")
                    Result.failure(Exception("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요."))
                }
                else -> {
                    Result.failure(Exception("팔로우 중 오류가 발생했습니다: ${e.message}"))
                }
            }
        }
    }

    suspend fun blockUser(userId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "사용자 차단 API 호출 시작: userId=$userId")
            
            val response = apiService.blockUser(userId)
            
            Log.d("RecommendedFriendRepository", "사용자 차단 API 응답 코드: ${response.code()}")
            
            when (response.code()) {
                200 -> {
                    val result = response.body() ?: "차단 완료 (userId=$userId)"
                    Log.d("RecommendedFriendRepository", "사용자 차단 성공: $result")
                    Result.success(result)
                }
                500 -> {
                    Log.e("RecommendedFriendRepository", "서버 내부 오류 (500) - 이미 차단된 사용자일 가능성")
                    Result.failure(Exception("이미 차단된 사용자이거나 차단할 수 없는 상태입니다."))
                }
                else -> {
                    Log.e("RecommendedFriendRepository", "사용자 차단 API 호출 실패: ${response.code()}")
                    Result.failure(Exception("차단에 실패했습니다. (오류 코드: ${response.code()})"))
                }
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "사용자 차단 API 호출 중 예외 발생", e)
            
            // EOFException (응답 본문 읽기 실패) 처리
            when (e) {
                is java.io.EOFException -> {
                    Log.e("RecommendedFriendRepository", "응답 본문 읽기 실패 - 서버 응답 문제")
                    Result.failure(Exception("서버 응답을 읽을 수 없습니다. 잠시 후 다시 시도해주세요."))
                }
                is java.net.SocketTimeoutException -> {
                    Log.e("RecommendedFriendRepository", "네트워크 타임아웃")
                    Result.failure(Exception("네트워크 연결이 지연되고 있습니다. 잠시 후 다시 시도해주세요."))
                }
                is java.net.UnknownHostException -> {
                    Log.e("RecommendedFriendRepository", "서버 연결 실패")
                    Result.failure(Exception("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요."))
                }
                else -> {
                    Result.failure(Exception("차단 중 오류가 발생했습니다: ${e.message}"))
                }
            }
        }
    }

    suspend fun reportFriend(reportedId: Int, reason: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("RecommendedFriendRepository", "사용자 신고 API 호출 시작: reportedId=$reportedId")

            val response = apiService.reportFriend(reportedId, ReportRequest(reason))

            Log.d("RecommendedFriendRepository", "사용자 신고 API 응답 코드: ${response.code()}")

            when (response.code()) {
                200 -> {
                    val result = response.body() ?: "신고가 접수되었습니다."
                    Log.d("RecommendedFriendRepository", "사용자 신고 성공: $result")
                    Result.success(result)
                }
                else -> {
                    Log.e("RecommendedFriendRepository", "사용자 신고 API 호출 실패: ${response.code()}")
                    Result.failure(Exception("신고에 실패했습니다. (오류 코드: ${response.code()})"))
                }
            }
        } catch (e: Exception) {
            Log.e("RecommendedFriendRepository", "사용자 신고 API 호출 중 예외 발생", e)
            when (e) {
                is java.io.EOFException -> Result.failure(Exception("서버 응답을 읽을 수 없습니다. 잠시 후 다시 시도해주세요."))
                is java.net.SocketTimeoutException -> Result.failure(Exception("네트워크 연결이 지연되고 있습니다. 잠시 후 다시 시도해주세요."))
                is java.net.UnknownHostException -> Result.failure(Exception("서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요."))
                else -> Result.failure(Exception("신고 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }
} 