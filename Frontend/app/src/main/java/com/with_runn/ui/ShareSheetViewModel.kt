package com.with_runn.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.with_runn.Event
import com.with_runn.share.ChatRepository
import com.with_runn.ui.chat.model.ChatRoom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShareSheetViewModel : ViewModel() {

    private val repo = ChatRepository()
    private val shareRepo = com.with_runn.share.ShareRepository()

    private val _rooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val rooms = _rooms.asStateFlow()

    // 공유 성공 시 채팅방 이동을 외부로 알리기 위한 이벤트
    private val _navigateEvent = MutableLiveData<Event<ChatRoom>>()
    val navigateEvent: LiveData<Event<ChatRoom>> = _navigateEvent

    suspend fun loadRooms() {
        val r = runCatching { repo.getRooms() }.getOrElse { emptyList() }
        android.util.Log.d("ShareVM", "emit size=${r.size}")
        _rooms.value = r
    }

    suspend fun shareCourseToRoom(userId: Int, courseId: Int, roomId: Int): Boolean {
        return shareRepo.shareCourse(userId = userId, chatId = roomId, courseId = courseId)
    }

    fun emitNavigateToRoomIfSuccess(success: Boolean, room: ChatRoom) {
        if (success) _navigateEvent.value = Event(room)
    }
}
