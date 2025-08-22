package com.with_runn.mypage


// 공통 응답 래퍼
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val result: T,
    val success: Boolean
)

// 1) 프로필 수정 요청/응답 모델
data class ProfileUpdateRequest(
    val provinceId: Int? = null,
    val cityId: Int? = null,
    val townId: Int? = null,
    val name: String? = null,
    val gender: String? = null,
    val birth: String? = null,
    val breed: String? = null,
    val size: String? = null,
    val characters: List<String>? = null,
    val style: List<String>? = null,
    val introduction: String? = null
)

data class ProfileData(
    val provinceId: Int?,
    val cityId: Int?,
    val townId: Int?,
    val name: String?,
    val gender: String?,
    val birth: String?,
    val breed: String?,
    val size: String?,
    val characters: List<String>?,
    val style: List<String>?,
    val profileImage: String,
    val introduction: String?
)

// 코스 요약 (스크랩/좋아요/내 코스 공통 아이템)
data class CourseBrief(
    val courseId: Int,
    val courseName: String,
    // 서버가 JSON array를 문자열로 주는 케이스가 있어 원문을 보존
    val keyword: String?,
    val time: Int,
    val courseImage: String?,
    val location: String?,
    // 각 리스트마다 타임스탬프 필드명이 다름 → nullable로 두고 원문 필드 유지
    val scrapedAt: String? = null,
    val likedAt: String? = null,
    val createdAt: String? = null
)

// 3) 스크랩
data class ScrapListResult(
    val scrapList: List<CourseBrief>
)

// 5) 좋아요
data class LikeListResult(
    val likeList: List<CourseBrief>
)

// 6/7) 팔로우/팔로워 공통 아이템 & 결과
data class FollowUser(
    val targetUserId: Long,
    val name: String?,
    val profileImage: String?
)

data class FollowListResult(
    val count: Int,
    // 서버 스키마가 followers도 followings로 내려보내는 예시가 있어서 그대로 맞춤
    val followings: List<FollowUser>
)

// 8) 내 코스
data class MyCourseListResult(
    val myCourseList: List<CourseBrief>
)

// /api/users/profile 응답 result
data class UserProfileResult(
    val id: Int,
    val userId: Int,
    val provinceId: Int,
    val provinceName: String,
    val cityId: Int,
    val cityName: String,
    val townId: Int,
    val townName: String,
    val name: String,
    val gender: String,
    val birth: String,
    val breed: String,
    val size: String,
    val profileImage: String,
    val character: String,
    val style: String
)

// /api/users/profile 전체 응답
data class UserProfileResponse(
    val code: String,
    val message: String,
    val result: UserProfileResult,
    val success: Boolean
)