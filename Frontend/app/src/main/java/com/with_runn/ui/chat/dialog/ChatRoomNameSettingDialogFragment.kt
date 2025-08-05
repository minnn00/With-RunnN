package com.with_runn.ui.chat.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.with_runn.R
import com.with_runn.ui.chat.repository.ChatRepository
import android.util.Log

class ChatRoomNameSettingDialogFragment : DialogFragment() {

    private lateinit var chatRoomNameInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var setButton: TextView
    
    private var onNameSetListener: ((String?) -> Unit)? = null
    private val chatRepository = ChatRepository()
    private var chatId: Int = -1
    
    fun setOnNameSetListener(listener: (String?) -> Unit) {
        onNameSetListener = listener
    }
    
    fun setChatId(id: Int) {
        chatId = id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_chat_room_name_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dialog 설정
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        setupViews(view)
        setupClickListeners()
        setupTextWatcher()
    }

    private fun setupViews(view: View) {
        chatRoomNameInput = view.findViewById(R.id.chat_room_name_input)
        clearButton = view.findViewById(R.id.clear_button)
        setButton = view.findViewById(R.id.set_button)
    }

    private fun setupClickListeners() {
        // X 버튼 클릭 리스너
        clearButton.setOnClickListener {
            chatRoomNameInput.text.clear()
        }

        // 설정 버튼 클릭 리스너
        setButton.setOnClickListener {
            val roomName = chatRoomNameInput.text.toString().trim()
            if (roomName.isNotEmpty()) {
                // 실제 API 호출로 채팅방 이름 설정
                updateChatRoomName(roomName)
            } else {
                Toast.makeText(context, "채팅방 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextWatcher() {
        chatRoomNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()
                
                // X 버튼 표시/숨김
                clearButton.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
                
                // 설정 버튼 활성화/비활성화
                if (text.isNotEmpty()) {
                    setButton.apply {
                        background = resources.getDrawable(R.drawable.bg_button_main, null)
                        setTextColor(resources.getColor(android.R.color.white, null))
                    }
                } else {
                    setButton.apply {
                        background = resources.getDrawable(R.drawable.bg_button_inactive, null)
                        setTextColor(resources.getColor(R.color.gray_600, null))
                    }
                }
            }
        })
    }
    
    /**
     * 실제 API를 호출하여 채팅방 이름 설정
     */
    private fun updateChatRoomName(newName: String) {
        Log.d("ChatRoomNameSettingDialog", "=== 실제 채팅방 이름 설정 시작 ===")
        Log.d("ChatRoomNameSettingDialog", "설정할 이름: $newName")
        
        // 로딩 표시 (버튼 비활성화)
        setButton.isEnabled = false
        setButton.text = "설정 중..."
        
        if (chatId == -1) {
            Toast.makeText(context, "채팅방 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        
        chatRepository.updateChatName(chatId, newName) { result ->
            activity?.runOnUiThread {
                result.fold(
                    onSuccess = { response ->
                        Log.d("ChatRoomNameSettingDialog", "✅ 채팅방 이름 설정 성공!")
                        Log.d("ChatRoomNameSettingDialog", "응답: code=${response.code}, message=${response.message}")
                        
                        // 성공 처리
                        Toast.makeText(context, "채팅방 이름이 변경되었습니다", Toast.LENGTH_SHORT).show()
                        
                        // 콜백 호출 (새로 설정한 이름 전달)
                        onNameSetListener?.invoke(newName)
                        
                        // 다이얼로그 닫기
                        dismiss()
                    },
                    onFailure = { exception ->
                        Log.e("ChatRoomNameSettingDialog", "❌ 채팅방 이름 설정 실패", exception)
                        
                        // 실패 처리
                        Toast.makeText(context, "채팅방 이름 설정에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
                        
                        // 버튼 상태 복원
                        setButton.isEnabled = true
                        setButton.text = "설정"
                    }
                )
            }
        }
    }
} 