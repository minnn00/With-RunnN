package com.with_runn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.with_runn.databinding.FragmentLocalMoreBinding
import com.with_runn.viewmodel.LocalMoreViewModel

class LocalMoreFragment : Fragment() {

    private var _binding: FragmentLocalMoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocalMoreViewModel by viewModels()
    private lateinit var adapter: WalkCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WalkCourseAdapter(mutableListOf(),
            onItemClick = { item ->
                val bundle = Bundle().apply {
                    putParcelable("course", item)
                }
                findNavController().navigate(R.id.mapContainerFragment, bundle)
            }
        )

        binding.recyclerLocalMore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLocalMore.adapter = adapter

        viewModel.localCourses.observe(viewLifecycleOwner) { list ->
            adapter.updateItems(list)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
