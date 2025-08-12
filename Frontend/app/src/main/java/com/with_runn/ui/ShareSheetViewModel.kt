package com.with_runn.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.with_runn.Event
import com.with_runn.ui.chat.model.ChatRoom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShareSheetViewModel : ViewModel() {

    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms = _rooms.asStateFlow()

    // 공유 성공 시 채팅방 이동을 외부로 알리기 위한 이벤트
    private val _navigateEvent = MutableLiveData<Event<ChatRoom>>()
    val navigateEvent: LiveData<Event<ChatRoom>> = _navigateEvent

    suspend fun loadRooms() {
        // TODO: 실제 API 연동
        delay(200) // 샘플 딜레이

    }

    suspend fun shareCourseToRoom(courseId: Int, roomId: Int): Boolean {
        // TODO: 실제 공유 API 연동
        delay(300)
        return true // 실패 시 false
    }

    fun emitNavigateToRoomIfSuccess(success: Boolean, room: ChatRoom) {
        if (success) _navigateEvent.value = Event(room)
    }
}
