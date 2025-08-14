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
import com.with_runn.data.viewmodel.MyPageViewModel
import com.with_runn.data.viewmodel.MyPageViewModelFactory
import com.with_runn.data.toWalkCourse
import com.with_runn.ui.onboarding.UriToMultipart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private lateinit var adapter: MyPageCourseAdapter
    private lateinit var viewModel: MyPageViewModel

    private val activityVM : ActivityViewModel by activityViewModels()

    private var currentTab = TabType.SCRAP
    private var isDeleteMode = false
    private var isDeleteButtonVisible = false

    private var myCourseList = listOf<WalkCourse>()

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activityVM.setBottomNavVisibility(true)

        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyPageFragment", "onViewCreated 진입")

        // ViewModel 연결
        val repository = MyPageRepository(RetrofitInstance.myPageApi)
        val factory = MyPageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]


        Log.d("MyPageFragment", "ViewModel 생성됨")

        viewModel.loadScrapCourses()
        viewModel.loadLikedCourses()
        Log.d("MyPageFragment", "viewModel.loadScrapCourses() 호출됨")
        // Adapter 설정
        adapter = MyPageCourseAdapter(
            currentTab,
            isDeleteMode,
            onItemDeleteClick = { deletedItem ->
                val newList = myCourseList.toMutableList().apply {
                    remove(deletedItem)
                }
                myCourseList = newList
                adapter.submitList(newList)
            },
            onItemClicked = { clickedItem ->
                Log.d("MyPageFragment", "Clicked item: ${clickedItem.title}, currentTab=$currentTab, isDeleteMode=$isDeleteMode")
                if (currentTab == TabType.MY_COURSE && !isDeleteButtonVisible && !isDeleteMode) {
                    isDeleteButtonVisible = true
                    isDeleteMode = true
                    binding.btnDeleteMode.visibility = View.VISIBLE
                    adapter.setTabType(currentTab, isDeleteMode)
                    adapter.notifyDataSetChanged()
                }
            },
            onScrapClick = { item ->
                // 서버 기반이면 여기도 추후 삭제 API 연결 필요
            },
            onLikeClick = { item ->
                // 서버 기반이면 여기도 추후 삭제 API 연결 필요
            }
        )

        binding.recyclerMypage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMypage.adapter = adapter

        // LiveData observe
        viewModel.scrapList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.SCRAP) {
                adapter.submitList(list.map { it.toWalkCourse() }) //TODO 오류처리
            }
        }

        viewModel.likeList.observe(viewLifecycleOwner) { list ->
            if (currentTab == TabType.LIKE) {
                adapter.submitList(list.map { it.toWalkCourse() })//TODO 오류처리
            }
        }


        setupTabs()
        setupDeleteButtons()

        binding.layoutFollower.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("initialTab", 0) // 0: 팔로워, 1: 팔로우
            }
            findNavController().navigate(R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle)
        }
        binding.layoutFollowing.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("initialTab", 1) // 0: 팔로워, 1: 팔로우
            }
            findNavController().navigate(R.id.action_mypage_graph_to_mypageFollowerFollowFragment, bundle)
        }
        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_graph_to_mypageOptionFragment2)
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
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadScrapCourses()
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabLike.setOnClickListener {
            currentTab = TabType.LIKE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            viewModel.loadLikedCourses()
            updateTabUI()
            hideDeleteButtons()
        }

        binding.tabMycourses.setOnClickListener {
            currentTab = TabType.MY_COURSE
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            adapter.submitList(myCourseList.toList())
            updateTabUI()
            hideDeleteButtons()
        }
    }

    private fun setupDeleteButtons() {
        binding.btnDeleteMode.setOnClickListener {
            isDeleteMode = true
            adapter.setTabType(currentTab, isDeleteMode)
            binding.btnDeleteMode.visibility = View.GONE
            binding.btnDone.visibility = View.VISIBLE
        }

        binding.btnDone.setOnClickListener {
            isDeleteMode = false
            isDeleteButtonVisible = false
            adapter.setTabType(currentTab, isDeleteMode)
            hideDeleteButtons()
        }
    }

    private fun hideDeleteButtons() {
        binding.btnDeleteMode.visibility = View.GONE
        binding.btnDone.visibility = View.GONE
    }

    private fun updateTabUI() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.gray_400)
        val indicatorOn = ContextCompat.getColor(requireContext(), R.color.green_700)
        val indicatorOff = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        binding.tabScrap.setTextColor(if (currentTab == TabType.SCRAP) selectedColor else unselectedColor)
        binding.tabLike.setTextColor(if (currentTab == TabType.LIKE) selectedColor else unselectedColor)
        binding.tabMycourses.setTextColor(if (currentTab == TabType.MY_COURSE) selectedColor else unselectedColor)

        binding.indicatorScrap.setBackgroundColor(if (currentTab == TabType.SCRAP) indicatorOn else indicatorOff)
        binding.indicatorLike.setBackgroundColor(if (currentTab == TabType.LIKE) indicatorOn else indicatorOff)
        binding.indicatorMycourses.setBackgroundColor(if (currentTab == TabType.MY_COURSE) indicatorOn else indicatorOff)
    }
}