package com.with_runn.mypage


import okhttp3.MultipartBody
import retrofit2.http.*

interface MyPageApi {

    // 0) 프로필 조회
    @GET("api/users/profile")
    suspend fun getProfile(
        @Header("Authorization") accessToken: String
    ): UserProfileResponse

    // 1) 프로필 수정
    @PATCH("api/users/profile")
    suspend fun patchProfile(
        @Header("Authorization") accessToken: String,
        @Body body: ProfileUpdateRequest
    ): ApiResponse<ProfileData>

    // 2) 프로필 이미지 업로드 (multipart)
    @Multipart
    @POST("api/user/profile/image")
    suspend fun uploadProfileImage(
        @Header("Authorization") accessToken: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<String>

    // 3) 스크랩 목록
    @GET("api/users/scraps")
    suspend fun getScraps(
        @Header("Authorization") accessToken: String
    ): ApiResponse<ScrapListResult>

    // 5) 좋아요 목록
    @GET("api/users/likes")
    suspend fun getLikes(
        @Header("Authorization") accessToken: String
    ): ApiResponse<LikeListResult>

    // 6) 팔로잉 목록
    @GET("api/users/followings")
    suspend fun getFollowings(
        @Header("Authorization") accessToken: String
    ): ApiResponse<FollowListResult>

    // 7) 팔로워 목록
    @GET("api/users/followers")
    suspend fun getFollowers(
        @Header("Authorization") accessToken: String
    ): ApiResponse<FollowListResult>

    // 8) 내 코스 목록
    @GET("api/users/courses")
    suspend fun getMyCourses(
        @Header("Authorization") accessToken: String
    ): ApiResponse<MyCourseListResult>
}