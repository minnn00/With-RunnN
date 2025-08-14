package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.with_runn.data.TokenManager
import com.with_runn.data.WalkCourseResponse
import com.with_runn.data.repository.MyPageRepository
import kotlinx.coroutines.launch

class MyPageViewModel(private val repository: MyPageRepository) : ViewModel() {

    private val _scrapList = MutableLiveData<List<WalkCourseResponse>>()
    val scrapList: LiveData<List<WalkCourseResponse>> = _scrapList

    private val _likeList = MutableLiveData<List<WalkCourseResponse>>()
    val likeList: LiveData<List<WalkCourseResponse>> = _likeList

    fun loadScrapCourses() {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken()
            if (token?.isBlank() == true) {
                Log.e("MyPageViewModel", "토큰이 비어 있음!")
                return@launch
            }

            Log.d("토큰 값 확인", token.toString())

            try {
                val response = repository.getScrapCourses(token = "Bearer $token")

                Log.d("API 응답 코드", response.code().toString())
                Log.d("API 응답 바디", response.body().toString())
                Log.d("API 에러 바디", response.errorBody()?.string() ?: "에러 바디 없음")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val scrapItems = body.result?.scrapList ?: emptyList()
                        Log.d("MyPageViewModel", "스크랩 목록 ${scrapItems.size}개")

                        val walkCourseList = scrapItems.map { item ->
                            WalkCourseResponse(
                                id = item.courseId,
                                title = "임시 제목",        // 서버 연결 후 실제 title 사용
                                imageUrl = "",
                                tags = listOf(),
                                distanceMeters = 0,
                                durationMinutes = 0,
                                isScrapped = true,
                                isLiked = false
                            )
                        }
                        _scrapList.value = walkCourseList
                    } else {
                        Log.e("MyPageViewModel", "response.body()가 null임")
                    }
                } else {
                    val error = response.errorBody()?.string() ?: "응답 실패 - 에러 바디 없음"
                    Log.e("MyPageViewModel", "API 실패: ${response.code()} / $error")
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "API 예외 발생: ${e.message}", e)
            }
        }
    }

    fun loadLikedCourses() {
        viewModelScope.launch {
            val token = TokenManager.getAccessToken()
            if (token?.isBlank() == true) {
                Log.e("MyPageViewModel", "토큰이 비어 있음! (좋아요)")
                return@launch
            }

            Log.d("토큰 확인", token.toString())

            try {
                val response = repository.getLikedCourses("Bearer $token")

                Log.d("좋아요 응답 코드", response.code().toString())
                Log.d("좋아요 응답 바디", response.body().toString())
                Log.d("좋아요 에러 바디", response.errorBody()?.string() ?: "에러 바디 없음")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val likeItems = body.result?.likeList ?: emptyList()
                        Log.d("MyPageViewModel", "좋아요 목록 ${likeItems.size}개")

                        val walkCourseList = likeItems.map { item ->
                            WalkCourseResponse(
                                id = item.courseId,
                                title = "임시 제목",
                                imageUrl = "",
                                tags = listOf(),
                                distanceMeters = 0,
                                durationMinutes = 0,
                                isScrapped = false,
                                isLiked = true
                            )
                        }
                        _likeList.value = walkCourseList
                    } else {
                        Log.e("MyPageViewModel", "좋아요 response.body()가 null임")
                    }
                } else {
                    val error = response.errorBody()?.string() ?: "응답 실패 - 에러 바디 없음"
                    Log.e("MyPageViewModel", "좋아요 API 실패: ${response.code()} / $error")
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "좋아요 API 예외 발생: ${e.message}", e)
            }
        }
    }
}