package com.with_runn.ui.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.with_runn.R
import com.with_runn.data.Friend
import com.with_runn.data.Gender
import com.with_runn.databinding.FragmentFriendsSeeAllBinding

class FriendsSeeAllFragment : Fragment() {

    private var _binding: FragmentFriendsSeeAllBinding? = null
    private val binding get() = _binding!!

    private lateinit var friendsAdapter: FriendsAdapter
    private var allFriends: List<Friend> = emptyList()
    private var filteredFriends: List<Friend> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsSeeAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupRecyclerView()
        setupSearchView()
        loadFriends()
    }

    private fun setupViews() {
        // 필요한 초기 설정
    }

    private fun setupRecyclerView() {
        friendsAdapter = FriendsAdapter { friend ->
            showFriendDetail(friend)
        }

        binding.rvFriendsList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = friendsAdapter
        }
    }



    private fun setupSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                filterFriends(query)
            }
        })
    }



    private fun loadFriends() {
        // 임시 데이터 생성
        allFriends = createSampleFriends()
        filteredFriends = allFriends
        friendsAdapter.submitList(filteredFriends)
    }

    private fun createSampleFriends(): List<Friend> {
        return listOf(
            Friend(
                id = "1",
                name = "마루",
                imageUrl = "https://example.com/image1.jpg",
                tags = listOf("장난꾸러기", "대인관계", "활발함"),
                bio = "안녕하세요! 저는 활발하고 친근한 골든리트리버 마루입니다.",
                age = "2년 4개월",
                breed = "골든리트리버",
                category = "중형견",
                gender = Gender.MALE,
                isFollowing = false
            ),
            Friend(
                id = "2",
                name = "보리",
                imageUrl = "https://example.com/image2.jpg",
                tags = listOf("조용함", "차분함", "온순함"),
                bio = "차분하고 온순한 성격의 보리입니다. 조용한 곳을 좋아해요.",
                age = "1년 8개월",
                breed = "시바견",
                category = "소형견",
                gender = Gender.FEMALE,
                isFollowing = true
            ),
            Friend(
                id = "3",
                name = "초코",
                imageUrl = "https://example.com/image3.jpg",
                tags = listOf("똑똑함", "민첩함", "훈련잘함"),
                bio = "똑똑하고 민첩한 초코입니다. 훈련받는 것을 좋아해요!",
                age = "3년 1개월",
                breed = "보더콜리",
                category = "중형견",
                gender = Gender.MALE,
                isFollowing = false
            ),
            Friend(
                id = "4",
                name = "루비",
                imageUrl = "https://example.com/image4.jpg",
                tags = listOf("사교적", "친화적", "에너지넘침"),
                bio = "사교적이고 친화적인 루비입니다. 모든 친구들과 잘 어울려요!",
                age = "2년 7개월",
                breed = "래브라도 리트리버",
                category = "대형견",
                gender = Gender.FEMALE,
                isFollowing = false
            ),
            Friend(
                id = "5",
                name = "코코",
                imageUrl = "https://example.com/image5.jpg",
                tags = listOf("귀여움", "애교", "작은체구"),
                bio = "작지만 애교가 많은 코코입니다. 귀여운 매력으로 가득해요!",
                age = "1년 3개월",
                breed = "치와와",
                category = "소형견",
                gender = Gender.FEMALE,
                isFollowing = true
            ),
            Friend(
                id = "6",
                name = "맥스",
                imageUrl = "https://example.com/image6.jpg",
                tags = listOf("충성심", "보호본능", "든든함"),
                bio = "충성심이 강하고 든든한 맥스입니다. 언제나 가족을 보호해요.",
                age = "4년 2개월",
                breed = "저먼 셰퍼드",
                category = "대형견",
                gender = Gender.MALE,
                isFollowing = false
            )
        )
    }

    private fun filterFriends(query: String) {
        filteredFriends = if (query.isEmpty()) {
            allFriends
        } else {
            allFriends.filter { friend ->
                friend.name.contains(query, ignoreCase = true) ||
                friend.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                friend.breed.contains(query, ignoreCase = true)
            }
        }
        friendsAdapter.submitList(filteredFriends)
    }

    private fun showFriendDetail(friend: Friend) {
        val dialog = FriendDetailDialogFragment.newInstance(friend)
        dialog.show(parentFragmentManager, "FriendDetailDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 