package com.with_runn.ui.map.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.with_runn.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchResultAdapter: SearchResultAdapter
    private var onPlaceSelected: ((SearchResultItem) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchResultAdapter = SearchResultAdapter(
            onItemClick = { item -> onClickItem(item) }
        )

        binding.searchResultRcv.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onClickItem(item: SearchResultItem) {
        Log.d("TEST", "${item.name} clicked!")
        onPlaceSelected?.invoke(item)
    }

    fun setResults(results: List<SearchResultItem>) {
        if (this::searchResultAdapter.isInitialized) {
            searchResultAdapter.submitList(results)
        }
    }

    fun setOnPlaceSelectedListener(listener: (SearchResultItem) -> Unit) {
        this.onPlaceSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
