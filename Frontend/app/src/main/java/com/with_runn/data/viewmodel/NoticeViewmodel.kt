package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.with_runn.data.model.Notice
import com.with_runn.data.repository.NoticeRepository
import kotlinx.coroutines.launch

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    private val _notices = MutableLiveData<List<Notice>>()
    val notices: LiveData<List<Notice>> get() = _notices

    fun loadNotices() {
        viewModelScope.launch {
            try {
                val result = repository.fetchNotices()
                _notices.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class NoticeViewModelFactory(private val repository: NoticeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
