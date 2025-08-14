package com.with_runn.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.CourseDetailResponse
import com.with_runn.data.ShareRequest
import com.with_runn.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseDetailViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courseDetail = MutableLiveData<CourseDetailResponse?>()
    val courseDetail: LiveData<CourseDetailResponse?> = _courseDetail

    private val _likeMessage = MutableLiveData<String>()
    val likeMessage: LiveData<String> = _likeMessage

//    fun fetchCourseDetail(courseId: Int) {
//        viewModelScope.launch {
//            val result = repository.getCourseDetail(courseId)
//            // result가 null일 수도 있으니 아래처럼 안전하게 처리
//            if (result != null) {
//                _courseDetail.value = result
//            } else {
//                // 필요하다면 에러 상태나 기본값 처리
//            }
//        }
//    }
//
//
//    fun postLike(courseId: Int) {
//        viewModelScope.launch {
//            try {
//                Log.d("LikeAPI", "좋아요 요청: courseId=$courseId") // 1. 요청 시작 로그
//
//                val response = repository.postLike(courseId) // (또는 LikeRequest(userId, courseId) 등)
//
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    Log.d("LikeAPI", "성공: ${body?.message} (courseId=${body?.courseId})") // 2. 성공 로그
//                } else {
//                    Log.e("LikeAPI", "실패: ${response.code()} / ${response.errorBody()?.string()}") // 3. 실패 로그
//                }
//            } catch (e: Exception) {
//                Log.e("LikeAPI", "예외 발생: ${e.localizedMessage}") // 4. 예외 로그
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun postScrap(courseId: Int, onResult: (String) -> Unit) {
//        viewModelScope.launch {
//            Log.d("ScrapAPI", "스크랩 요청 시작: courseId=$courseId")
//            try {
//                val response = repository.postScrap(courseId)
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    Log.d("ScrapAPI", "성공! 응답: $body")
//                    onResult(body?.message ?: "알 수 없는 응답")
//                } else {
//                    val errorMsg = response.errorBody()?.string()
//                    Log.e("ScrapAPI", "실패: ${response.code()} / $errorMsg")
//                    onResult("스크랩 실패: $errorMsg")
//                }
//            } catch (e: Exception) {
//                Log.e("ScrapAPI", "예외 발생: ${e.localizedMessage}")
//                onResult("에러: ${e.localizedMessage}")
//            }
//        }
//    }
//
//    fun postShareCourse(
//        isChat: Boolean,
//        userId: Int,
//        targetUserId: Int?,
//        chatId: Int?,
//        courseId: Int,
//        onResult: (Boolean, String?) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                val request = ShareRequest(isChat, userId, targetUserId, chatId, courseId)
//                val response = repository.postShareCourse(request)
//                Log.d("ShareAPI", "공유 응답 코드: ${response.code()}") // ← 상태코드(200, 403 등) 확인
//                Log.d("ShareAPI", "공유 응답 body: ${response.body()}") // ← 정상 응답 내용
//                Log.d("ShareAPI", "공유 에러 body: ${response.errorBody()?.string()}")
//                if (response.isSuccessful) {
//                    Log.d("ShareAPI", "공유 성공: ${response.body()}")
//                    onResult(true, response.body()?.msg)
//                } else {
//                    Log.e("ShareAPI", "공유 실패: ${response.errorBody()?.string()}")
//                    onResult(false, response.errorBody()?.string())
//                }
//            } catch (e: Exception) {
//                Log.e("ShareAPI", "공유 에러: ${e.localizedMessage}")
//                onResult(false, e.localizedMessage)
//            }
//        }
//    }

//    fun deleteScrap(courseId: Int, onResult: (String) -> Unit) {
//        viewModelScope.launch {
//            try {
//                Log.d("DeleteScrapAPI", "스크랩 취소 요청: courseId=$courseId")
//                val response = repository.deleteScrap(courseId)
//                if (response.isSuccessful) {
//                    val body = response.body()
//                    Log.d("DeleteScrapAPI", "성공: ${body?.message}")
//                    onResult(body?.message ?: "성공")
//                } else {
//                    Log.e("DeleteScrapAPI", "실패: ${response.code()} / ${response.errorBody()?.string()}")
//                    onResult("실패: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DeleteScrapAPI", "예외: ${e.localizedMessage}")
//                onResult("에러: ${e.localizedMessage}")
//            }
//        }
//    }

}
