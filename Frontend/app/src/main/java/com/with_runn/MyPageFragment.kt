package com.with_runn

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.with_runn.databinding.FragmentMypageBinding
import com.with_runn.ui.course.TabType
import com.with_runn.data.WalkCourse
import com.with_runn.data.model.setProfileImgResponse
import com.with_runn.data.network.ApiClient
import com.with_runn.data.remote.RetrofitInstance
import com.with_runn.data.repository.MyPageRepository
import com.with_runn.data.toWalkCourse
import com.with_runn.data.viewmodel.MyPageViewModel
import com.with_runn.data.viewmodel.MyPageViewModelFactory
import com.with_runn.data.toWalkCourse
import com.with_runn.ui.onboarding.UriToMultipart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import com.with_runn.data.WalkCourseResponse
import com.with_runn.data.MyCourse

private const val NO_KM = "- km"
private fun renderKm(meters: Int?): String =
    when {
        meters == null || meters <= 0 -> NO_KM
        meters % 1000 == 0            -> "${meters / 1000}km"
        else                          -> String.format("%.1fkm", meters / 1000.0)
    }

class MyPageFragment : Fragment() {



    private lateinit var binding: FragmentMypageBinding
    private lateinit var adapter: MyPageCourseAdapter
    private lateinit var viewModel: MyPageViewModel

    private val activityVM: ActivityViewModel by activityViewModels()

    private var currentTab = TabType.SCRAP
    private var isDeleteMode = false
    private var myCourseList: List<WalkCourse> = emptyList()

    private fun kmStringFromAny(raw: Any?): String {
        val meters: Int = when (raw) {
            null      -> 0
            is Int    -> raw
            is String -> raw.filter { it.isDigit() }.toIntOrNull() ?: 0
            else      -> 0
        }
        return if (meters <= 0) {
            "0km"                                  // null 대신 "0km"
        } else if (meters % 1000 == 0) {
            "${meters / 1000}km"
        } else {
            String.format("%.1fkm", meters / 1000.0)
        }
    }
    private fun minuteStringFromAny(raw: Any?): String? = when (raw) {
        null   -> null
        is Int -> if (raw > 0) "${raw}분" else null
        is String -> {
            val parts = raw.split(":")
            val minutes = if (parts.size == 3) {
                (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
            } else raw.filter { it.isDigit() }.toIntOrNull() ?: 0
            if (minutes > 0) "${minutes}분" else null
        }
        else -> null
    }

    private fun normalizeImageUrl(raw: String?): String? {
        val v = raw?.trim()
        if (v.isNullOrEmpty() || v.equals("string", true)) return null
        return if (v.startsWith("http", true)) v else "http://13.209.75.209:8080/$v"
    }

    // ----- WalkCourseResponse -> UI용 WalkCourse (마이페이지 전용) -----
    private fun WalkCourseResponse.toUiForMyPage(): WalkCourse = WalkCourse(
        id         = id,
        title      = title,
        tags       = tags ?: emptyList(),
        imageResId = R.drawable.image,
        imageUrl   = normalizeImageUrl(imageUrl),
        distance   = renderKm(distanceMeters),          // 없으면 – km
        time       = minuteStringFromAny(durationMinutes),
        isScrapped = isScrapped,
        isLiked    = isLiked
    )

    private fun kmStringFromMeters(meters: Int?): String {
        val m = meters ?: 0
        return if (m <= 0) "0km"
        else if (m % 1000 == 0) "${m / 1000}km"
        else String.format("%.1fkm", m / 1000.0)
    }

    // MyCourse → UI용 WalkCourse (마이페이지 전용)
    private fun MyCourse.toUiForMyPage(): WalkCourse = WalkCourse(
        id         = courseId,
        title      = courseName,
        tags       = parseTags(keyword),
        imageResId = 0,
        imageUrl   = courseImage,
        distance   = when {
            distanceMeters != null          -> renderKm(distanceMeters)     // 숫자로 오면 포맷
            !distanceText.isNullOrBlank()   -> distanceText!!               // "1.3km" 같이 오면 그대로
            else                            -> NO_KM                        // 없으면 – km
        },
        time       = minuteStringFromAny(time),          // ★ 여기 toMinuteString 아님!
        isScrapped = false,
        isLiked    = false
    )

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activityVM.setBottomNavVisibility(true)
        activityVM.setUpperToolbarVisibility(false)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyPageFragment", "onViewCreated")

        // ViewModel
        val repository = MyPageRepository(
            RetrofitInstance.myPageApi,
            RetrofitInstance.courseApi
        )
        val factory = MyPageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]

        // 프로필
        viewModel.loadUserProfile()

        // 초기 데이터
        viewModel.loadScrapCourses()
        viewModel.loadLikedCourses()

        // Adapter (아이템 제거는 어댑터가 처리 / 콜백은 API 호출/로그용)
        adapter = MyPageCourseAdapter(
            tabType = currentTab,
            isDeleteMode = isDeleteMode,
            onItemDeleteClick = { deletedItem ->
                // 내 코스 탭에서 삭제 시 내 로컬 목록도 동기화
                myCourseList = myCourseList.filterNot { it.id == deletedItem.id }
                showEditButtonsIfMyCourse()
                Log.d("MyPageFragment", "내 코스 삭제 id=${deletedItem.id}")
                // TODO: 서버 삭제 API 연결
            },
            onItemClicked = { /* 필요 시 상세 이동 */ },
            onScrapClick = { removed ->
                Log.d("MyPageFragment", "스크랩 해제 id=${removed.id}")
                // TODO: 스크랩 해제 API 연결
            },
            onLikeClick = { removed ->
                Log.d("MyPageFragment", "좋아요 해제 id=${removed.id}")
                // TODO: 좋아요 해제 API 연결
            }
        )
        binding.recyclerMypage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMypage.adapter = adapter

        // 프로필 observe
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profile.collect { profile ->

                val characters = parseTags(profile?.character)
                val styles     = parseTags(profile?.style)

                binding.apply {
                    tvMetaBreed.text = profile?.breed ?: "견종"
                    tvMetaAge.text   = profile?.birth?.toAgePeriodLegacy() ?: "나이"
                    tvMetaSize.text  = profile?.size ?: "크기"
                    textName.text    = profile?.name ?: "이름"

                    Glide.with(root.context)
                        .load(profile?.profileImage?.takeIf { !it.isNullOrBlank() })
                        .circleCrop()
                        .into(binding.imageProfile)

                    // 산책 스타일
                    tvStyleMain.text = styles.firstOrNull() ?: "산책 스타일"
                    val styleRest = (styles.size - 1).coerceAtLeast(0)
                    tvStyleMore.text = if (styleRest > 0) "외 ${styleRest}개" else ""
                    tvStyleMore.visibility = if (styleRest > 0) View.VISIBLE else View.GONE

                    // 성격
                    tvCharMain.text = characters.firstOrNull() ?: "성격"
                    val charRest = (characters.size - 1).coerceAtLeast(0)
                    tvCharMore.text = if (charRest > 0) "외 ${charRest}개" else ""
                    tvCharMore.visibility = if (charRest > 0) View.VISIBLE else View.GONE
                }
            }
        }


        // 리스트 observe
        viewModel.scrapList.observe(viewLifecycleOwner) { list ->
            android.util.Log.d("MyPage", "first=${list.firstOrNull()}")
            if (currentTab == TabType.SCRAP) {
                val mapped = list.map { it.toUiForMyPage() }
                Log.d("MyPageFragment", "SCRAP mapped first=${mapped.firstOrNull()}")
                adapter.submitList(mapped)
            }
        }
        viewModel.likeList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.LIKE) {
                val mapped = list.map { it.toUiForMyPage() }
                Log.d("MyPageFragment", "LIKE mapped first=${mapped.firstOrNull()}")
                adapter.submitList(mapped)
            }
        }
        viewModel.myCourseList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.MY_COURSE) {
                val ui = list.map { it.toUiForMyPage() }   //
                myCourseList = ui
                adapter.submitList(ui)
                showEditButtonsIfMyCourse()
            }
        }

        setupTabs()
        setupEditToggleButtons()

        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_graph_to_mypageOptionFragment)
        }
        binding.layoutFollower.setOnClickListener {
            val bundle = Bundle().apply { putInt("initialTab", 0) }
            findNavController().navigate(
                R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle
            )
        }
        binding.layoutFollowing.setOnClickListener {
            val bundle = Bundle().apply { putInt("initialTab", 1) }
            findNavController().navigate(
                R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle
            )
        }
    }

    override fun onResume() {
        super.onResume()
        showEditButtonsIfMyCourse()
    }

    fun String.toAgePeriodLegacy(): String {
        return try {
            val parts = this.split("-")
            if (parts.size != 3) return this

            val by = parts[0].toInt()
            val bm = parts[1].toInt() - 1  // Calendar 월은 0부터
            val bd = parts[2].toInt()

            val birth = GregorianCalendar(by, bm, bd)
            val now = Calendar.getInstance()

            var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
            val days = now.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

            if (days < 0) months -= 1
            if (months < 0) { years -= 1; months += 12 }

            when {
                years > 0 && months > 0 -> "${years}년 ${months}개월"
                years > 0 -> "${years}년"
                months > 0 -> "${months}개월"
                else -> "0개월"
            }
        } catch (_: Exception) {
            this
        }
    }

    fun parseTags(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        val s = raw.trim()

        return when {
            // [ "활발", "온화" ] 같은 형태
            s.startsWith("[") && s.endsWith("]") -> {
                s.substring(1, s.length - 1)
                    .split(',')
                    .map { it.trim().trim('"', '“', '”', '\'') }
                    .filter { it.isNotBlank() }
            }
            // "활발, 온화" 같은 형태
            s.contains(",") -> s.split(',')
                .map { it.trim().trim('"', '“', '”', '\'') }
                .filter { it.isNotBlank() }

            // 단일 태그
            else -> listOf(s.trim().trim('"', '“', '”', '\''))
        }
        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_graph_to_mypageOptionFragment)
        }
        binding.btnEditProfile.setOnClickListener {
            val permission = android.Manifest.permission.READ_MEDIA_IMAGES
            requestPermissionLauncher.launch(permission)
        }


    }

    // 1️⃣ 권한 요청
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("권한 필요")
                    .setMessage("이미지를 업로드하려면 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
                    .setPositiveButton("설정") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }

    // 2️⃣ 갤러리에서 이미지 선택
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this).load(it).circleCrop().into(binding.imageProfile)
                Log.d("Upload", (selectedImageUri != null).toString())
                if (selectedImageUri != null) {
                    val img = UriToMultipart.create(requireContext(), selectedImageUri!!)
                    ApiClient.instance.uploadProfileImage(img)
                        .enqueue(object : Callback<setProfileImgResponse> {
                            override fun onResponse(
                                call: Call<setProfileImgResponse>,
                                response: Response<setProfileImgResponse>
                            ) {
                                if (response.isSuccessful && response.body()?.success == true) {
                                    Log.d("Upload", "이미지 업로드 성공: ${response.body()?.result}")
                                } else {
                                    Log.e("Upload", "이미지 업로드 실패: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(
                                call: Call<setProfileImgResponse>,
                                t: Throwable
                            ) {
                                Log.e("Login", "오류 발생: ${t.message}")
                            }
                        })
                }
            }
        }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }


    private fun setupTabs() {
        binding.tabScrap.setOnClickListener {
            currentTab = TabType.SCRAP
            exitDeleteMode()
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadScrapCourses()
            updateTabUI()
            hideEditButtons()
        }
        binding.tabLike.setOnClickListener {
            currentTab = TabType.LIKE
            exitDeleteMode()
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadLikedCourses()
            updateTabUI()
            hideEditButtons()
        }
        binding.tabMycourses.setOnClickListener {
            currentTab = TabType.MY_COURSE
            exitDeleteMode()
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadMyCourses()
            adapter.submitList(myCourseList.toList())
            updateTabUI()
            showEditButtonsIfMyCourse()
        }
    }

    private fun setupEditToggleButtons() {
        binding.btnDeleteMode.setOnClickListener {
            if (currentTab != TabType.MY_COURSE) return@setOnClickListener
            enterDeleteMode()
        }
        binding.btnDone.setOnClickListener { exitDeleteMode() }
    }

    private fun enterDeleteMode() {
        isDeleteMode = true
        adapter.setTabType(currentTab, isDeleteMode)
        adapter.notifyDataSetChanged()
        binding.btnDeleteMode.visibility = View.GONE
        binding.btnDone.visibility = View.VISIBLE
        Log.d("MyPageFragment", "DeleteMode=ON")
    }

    private fun exitDeleteMode() {
        isDeleteMode = false
        adapter.setTabType(currentTab, isDeleteMode)
        adapter.notifyDataSetChanged()
        binding.btnDone.visibility = View.GONE
        binding.btnDeleteMode.visibility =
            if (currentTab == TabType.MY_COURSE && myCourseList.isNotEmpty()) View.VISIBLE else View.GONE
        Log.d("MyPageFragment", "DeleteMode=OFF")
    }

    private fun hideEditButtons() {
        binding.btnDeleteMode.visibility = View.GONE
        binding.btnDone.visibility = View.GONE
    }

    private fun showEditButtonsIfMyCourse() {
        if (currentTab == TabType.MY_COURSE && myCourseList.isNotEmpty()) {
            binding.btnDeleteMode.visibility = if (isDeleteMode) View.GONE else View.VISIBLE
            binding.btnDone.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
        } else {
            hideEditButtons()
        }
    }

    private fun updateTabUI() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.gray_400)
        val indicatorOn = ContextCompat.getColor(requireContext(), R.color.green_700)
        val indicatorOff = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        binding.tabScrap.setTextColor(
            if (currentTab == TabType.SCRAP) selectedColor else unselectedColor
        )
        binding.tabLike.setTextColor(
            if (currentTab == TabType.LIKE) selectedColor else unselectedColor
        )
        binding.tabMycourses.setTextColor(
            if (currentTab == TabType.MY_COURSE) selectedColor else unselectedColor
        )

        binding.indicatorScrap.setBackgroundColor(
            if (currentTab == TabType.SCRAP) indicatorOn else indicatorOff
        )
        binding.indicatorLike.setBackgroundColor(
            if (currentTab == TabType.LIKE) indicatorOn else indicatorOff
        )
        binding.indicatorMycourses.setBackgroundColor(
            if (currentTab == TabType.MY_COURSE) indicatorOn else indicatorOff
        )
    }

    // 작은 헬퍼
    private fun moreSuffix(list: List<String>): String {
        val more = (list.size - 1).coerceAtLeast(0)
        return if (more > 0) "외 ${more}개" else ""
    }
}
