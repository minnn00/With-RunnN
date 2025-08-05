package com.with_runn.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.with_runn.R
import com.with_runn.ui.adapter.DogCard
import com.with_runn.ui.adapter.DogCardAdapter
import com.with_runn.ui.chat.activity.ChatRoomActivity
import com.with_runn.ui.chat.repository.ChatRepository
import com.with_runn.ui.friend.DogCardMainActivity
import android.util.Log
import android.widget.Toast

class RecommendedFriendsFragment : Fragment() {
    private lateinit var dogCardAdapter: DogCardAdapter
    private var currentPage = 0
    private val chatRepository = ChatRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommended_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 저장된 페이지 상태 복원
        savedInstanceState?.let {
            currentPage = it.getInt("current_page", 0)
        }
        
        setupViewPager()
    }

    private fun setupViewPager() {
        // 4개의 강아지 카드 데이터 생성 (모두 jonny.png 사용!)
        val dogCards = listOf(
            DogCard(
                name = "조니",
                tag = "#에너지 폭발",
                imageResId = R.drawable.jonny,
                tags = listOf("#에너지 폭발", "#강아지 친구 찾기형")
            ),
            DogCard(
                name = "밀리",
                tag = "#호기심 왕성",
                imageResId = R.drawable.jonny,
                tags = listOf("#차분함", "#고집 셈")
            ),
            DogCard(
                name = "호두",
                tag = "#그치만안물어요",
                imageResId = R.drawable.jonny,
                tags = listOf("#낯가림", "#방어적")
            ),
            DogCard(
                name = "루시",
                tag = "#활발함",
                imageResId = R.drawable.jonny,
                tags = listOf("#친화적", "#장난기 많음")
            )
        )

        dogCardAdapter = DogCardAdapter(dogCards)
        val viewPager = view?.findViewById<ViewPager2>(R.id.cardViewpager)
        viewPager?.adapter = dogCardAdapter

        // 저장된 페이지로 이동
        viewPager?.setCurrentItem(currentPage, false)
        
        // 페이지 변경 리스너 추가
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updatePaginationIndicators(position)
            }
        })

        // 카드 클릭 리스너 추가
        dogCardAdapter.setOnItemClickListener { position: Int ->
            val dogCard = dogCards[position]

            val name = dogCard.name
            val tag = dogCard.tag
            val tags = ArrayList(dogCard.tags)
            val imageResId = dogCard.imageResId

            val dialogFragment = FriendProfileDialogFragment.newInstance(
                friendName = name,
                personalityTag = tag,
                personalityTags = tags,
                imageResId = imageResId
            )

            dialogFragment.setOnMessageButtonClickListener {
                // 채팅방 생성 API 호출
                createChatRoomWithFriend(dogCard)
            }

            dialogFragment.show(childFragmentManager, "FriendProfileDialog")
        }

    }
    
    /**
     * 추천 친구와 채팅방 생성
     */
    private fun createChatRoomWithFriend(dogCard: DogCard) {
        Log.d("RecommendedFriendsFragment", "=== 채팅방 생성 시작 ===")
        Log.d("RecommendedFriendsFragment", "선택된 친구: ${dogCard.name}")
        
        // 로딩 표시 (선택적으로 구현 가능)
        Toast.makeText(requireContext(), "${dogCard.name}님과 채팅방을 생성 중입니다...", Toast.LENGTH_SHORT).show()
        
        // 실제 로그인된 사용자 ID (백엔드 개발자 요청)
        val currentUserId = 10
        val targetUserId = getTargetUserId(dogCard.name) // 친구 이름으로 targetUserId 매핑
        
        Log.d("RecommendedFriendsFragment", "API 호출 파라미터: currentUserId=$currentUserId, targetUserId=$targetUserId")
        
        chatRepository.createChatRoomAndFind(currentUserId, targetUserId, dogCard.name) { result ->
            requireActivity().runOnUiThread {
                result.fold(
                    onSuccess = { chatRoom ->
                        if (chatRoom != null) {
                            Log.d("RecommendedFriendsFragment", "✅ 채팅방 생성 및 찾기 성공! chatId=${chatRoom.chatId}, name=${chatRoom.name}")
                            Toast.makeText(requireContext(), "${dogCard.name}님과의 채팅방이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            
                            // 생성된 채팅방으로 바로 이동
                            navigateToChatRoom(chatRoom.chatId, dogCard.name)
                        } else {
                            Log.d("RecommendedFriendsFragment", "⚠️ 생성된 채팅방을 찾을 수 없음")
                            Toast.makeText(requireContext(), "${dogCard.name}님과의 채팅방이 생성되었습니다!", Toast.LENGTH_SHORT).show()
                            
                            // 채팅 목록으로 이동
                            navigateToChatList()
                        }
                    },
                    onFailure = { exception ->
                        Log.e("RecommendedFriendsFragment", "❌ 채팅방 생성 실패", exception)
                        Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
    
    /**
     * 친구 이름으로 targetUserId 매핑 (API 명세서에 맞춤)
     */
    private fun getTargetUserId(friendName: String): Int {
        return when (friendName) {
            "조니" -> 11   // 새로운 ID 할당
            "밀리" -> 12   // 새로운 ID 할당
            "호두" -> 13   // 새로운 ID 할당
            "루시" -> 14   // 새로운 ID 할당
            else -> 15     // 기본값
        }
    }
    
    /**
     * 채팅 목록으로 이동
     */
    private fun navigateToChatList() {
        // BottomNavigationView에서 채팅 탭으로 이동
        val activity = requireActivity()
        if (activity is DogCardMainActivity) {
            activity.navigateToChatTab()
        }
    }
    
    /**
     * 생성된 채팅방으로 이동
     */
    private fun navigateToChatRoom(chatId: Int, friendName: String) {
        // 채팅방 입장 API가 아직 구현되지 않아서 바로 이동
        Log.d("RecommendedFriendsFragment", "채팅방 화면으로 이동: chatId=$chatId")
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("friend_name", friendName)
            putExtra("is_new_chat", true) // 새 채팅방 플래그
        }
        startActivity(intent)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("current_page", currentPage)
    }
    
    private fun updatePaginationIndicators(selectedPosition: Int) {
        val indicator0 = view?.findViewById<ImageView>(R.id.indicator_0)
        val indicator1 = view?.findViewById<ImageView>(R.id.indicator_1)
        val indicator2 = view?.findViewById<ImageView>(R.id.indicator_2)
        val indicator3 = view?.findViewById<ImageView>(R.id.indicator_3)
        val indicators = listOf(indicator0, indicator1, indicator2, indicator3)
        
        indicators.forEachIndexed { idx, imageView ->
            imageView?.setImageResource(
                if (idx == selectedPosition) R.drawable.ic_indicator_active
                else R.drawable.ic_indicator_inactive
            )
        }
    }
} 