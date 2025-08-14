package com.with_runn.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.ActivityViewModel
import com.with_runn.R
import com.with_runn.data.HotCourse
import com.with_runn.data.LocalCourse
import com.with_runn.data.viewmodel.WalkCourseViewModel
import com.with_runn.databinding.FragmentWalkCourseBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

class WalkCourseFragment : Fragment() {

    // ✅ 액티비티 스코프 ViewModel만 사용 (중복 생성 방지)
    private val viewModel: WalkCourseViewModel by activityViewModels()
    private val activityVM: ActivityViewModel by activityViewModels()

    private var _binding: FragmentWalkCourseBinding? = null
    private val binding get() = _binding!!

    private lateinit var localPreviewAdapter: LocalCourseAdapter
    private lateinit var risingPreviewAdapter: HotCourseAdapter

    private companion object {
        const val TAG = "WalkCourseFragment"
    }

    // ----- helpers -----
    private fun previewTitleFrom(name: String?): String {
        val n = name?.trim()
        return if (!n.isNullOrEmpty() && !n.equals("string", true)) n else "(제목 없음)"
    }

    private fun normalizeImageUrl(raw: String?): String? {
        val v = raw?.trim()
        if (v.isNullOrEmpty() || v.equals("string", true)) return null
        return if (v.startsWith("http", true)) v else "http://13.209.75.209:8080/$v"
    }

    private fun kmStringFromAny(meters: Int?, text: String?): String =
        when {
            meters != null -> {
                if (meters <= 0) "– km"
                else if (meters % 1000 == 0) "${meters / 1000}km"
                else String.format("%.1fkm", meters / 1000.0)
            }
            !text.isNullOrBlank() -> text
            else -> "– km"
        }

    private fun minuteStringFromAny(raw: Any?): String? = when (raw) {
        null -> null
        is Int -> if (raw > 0) "${raw}분" else null
        is String -> {
            val p = raw.split(":")
            val m = if (p.size == 3)
                (p[0].toIntOrNull() ?: 0) * 60 + (p[1].toIntOrNull() ?: 0)
            else
                raw.filter { it.isDigit() }.toIntOrNull() ?: 0
            if (m > 0) "${m}분" else null
        }
        else -> null
    }
    // --------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkCourseBinding.inflate(inflater, container, false)
        activityVM.setBottomNavVisibility(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === 우리동네 미리보기 (가로) ===
        localPreviewAdapter = LocalCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerLocalCourse.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            adapter = localPreviewAdapter
        }

        // === 떠오르는 미리보기 (세로) ===
        risingPreviewAdapter = HotCourseAdapter(mutableListOf()) { course ->
            val bundle = Bundle().apply { putInt("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.recyclerRisingCourse.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            adapter = risingPreviewAdapter
            isNestedScrollingEnabled = false
        }

        // === Observe: 우리동네 미리보기(태그/제목만) ===
        viewModel.neighborhoodPreview.observe(viewLifecycleOwner) { previewList ->
            Log.d(TAG, "우리동네 미리보기 size=${previewList.size} sample=${previewList.firstOrNull()}")
            val mapped = previewList.map { r ->
                val tagsFromArray = r.keyword?.filter { it.isNotBlank() }?.map { it.trim() } ?: emptyList()
                val tagsFromText = r.keywordText
                    ?.split(',', '#', ' ')
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()
                val tagText = (tagsFromArray + tagsFromText)
                    .distinct()
                    .take(2)
                    .joinToString(" ") { "#$it" }

                LocalCourse(
                    id = r.courseId,
                    title = previewTitleFrom(r.name),
                    tag = tagText,
                    imageRes = R.drawable.image,
                    imageUrl = normalizeImageUrl(r.courseImage)
                )
            }
            localPreviewAdapter.updateData(mapped.toMutableList())
        }

        // === Observe: 떠오르는 코스 미리보기 ===
        viewModel.risingPreview.observe(viewLifecycleOwner) { previewList ->
            risingPreviewAdapter.updateData(
                previewList.map { r ->
                    val distanceStr = kmStringFromAny(r.distanceMeters, r.distance)
                    val timeStr = when {
                        !r.time.isNullOrBlank() && r.time!!.contains("분") -> r.time!!
                        r.durationMinutes != null -> "${r.durationMinutes}분"
                        else -> minuteStringFromAny(r.time) ?: ""
                    }
                    HotCourse(
                        id = r.courseId,
                        title = r.name,
                        tags = r.keyword ?: emptyList(),
                        imageUrl = r.courseImage,
                        distance = distanceStr,
                        time = timeStr
                    )
                }.toMutableList()
            )
        }

        // === 초기 로딩: 데이터가 비어 있을 때만 호출 ===
        if (viewModel.risingPreview.value.isNullOrEmpty()) {
            Log.d(TAG, "초기 risingPreview 비어있음 → fetchRisingPreview()")
            viewModel.fetchRisingPreview()
        }
        activityVM.selectedProvinceId.value?.let { pid ->
            Log.d(TAG, "초기 provinceId=$pid → ensureHomePreviews(pid)")
            viewModel.ensureHomePreviews(pid)
        } ?: Log.d(TAG, "초기 provinceId 없음 → 우리동네 호출 보류")

        // === pID 변경 감지되면 그때만 재호출 ===
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityVM.selectedProvinceId
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { id ->
                    Log.d(TAG, "province changed → $id")
                    viewModel.fetchNeighborhoodPreview(id)
                }
        }

        // === 더보기 버튼 ===
        binding.textLocalMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_localMoreFragment)
        }
        binding.textRisingMore.setOnClickListener {
            findNavController().navigate(R.id.action_walkCourse_to_hotMoreFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
