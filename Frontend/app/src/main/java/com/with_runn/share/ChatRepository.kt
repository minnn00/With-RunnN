// com/with_runn/share/ChatRepository.kt
package com.with_runn.share

import android.util.Log
import com.with_runn.data.TokenManager
import com.with_runn.ui.chat.model.ChatRoom
import okio.IOException
import retrofit2.HttpException

class ChatRepository {
    suspend fun getRooms(): List<ChatRoom> = try {
        val bearer = "Bearer ${TokenManager.getAccessToken()}"
        val envelope = ChatService.api.getMyChatRooms(bearer)
        val list = envelope.result ?: emptyList()
        Log.d("ChatRepository", "rooms from API = ${list.size}")

        // 1) DTO 샘플 덤프(최대 3개)
        Log.d(
            "ChatRepository",
            "dto sample=" + list.take(3).joinToString { d ->
                "(id=${d.chatId}, name=${d.chatName}, last=${d.lastReceivedMsg}, unread=${d.unReadMsgCount}, names=${d.usernameList?.size}, pics=${d.userProfileList?.size})"
            }
        )

        list.map { dto ->
            val p1 = dto.userProfileList?.getOrNull(0)
            val p2 = dto.userProfileList?.getOrNull(1)
            ChatRoom(
                chatId = dto.chatId,
                name = dto.chatName.orEmpty(),
                time = dto.lastReceivedMsg.orEmpty(),
                lastMessage = "",
                notificationCount = dto.unReadMsgCount ?: 0,
                participants = dto.participants ?: (dto.usernameList?.size ?: 0),
                profileImageResId = 0,
                profileImage2ResId = 0,
                hasSecondImage = p2 != null,
                profileImageUrl = p1,
                profileImage2Url = p2
            )
        }.also { mapped ->
            Log.d("ChatRepository", "mapped count=${mapped.size}")
            mapped.take(3).forEachIndexed { idx, item ->
                Log.d(
                    "ChatRepository",
                    "mapped#$idx -> id=${item.chatId}, name='${item.name}', time='${item.time}', " +
                            "unread=${item.notificationCount}, parts=${item.participants}, second=${item.hasSecondImage}"
                )
            }
            val blankNames = mapped.count { it.name.isBlank() }
            val blankTimes = mapped.count { it.time.isBlank() }
            Log.d("ChatRepository", "quality blankNames=$blankNames, blankTimes=$blankTimes")
        }
    }catch (e: HttpException) {
        Log.e("ChatRepository", "HTTP ${e.code()}: ${e.message()}")
        emptyList()
    } catch (e: IOException) {
        Log.e("ChatRepository", "IO error: ${e.message}")
        emptyList()
    } catch (e: Exception) {
        Log.e("ChatRepository", "Unknown error: ${e.message}")
        emptyList()
    }
}
