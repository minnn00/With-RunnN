package com.with_runn.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.model.Follower
import com.with_runn.data.repository.MypageFollowerRepository
import kotlinx.coroutines.launch

class MypageFollowerViewmodel : ViewModel() {
    private val repository = MypageFollowerRepository()

    private val _followers = MutableLiveData<List<Follower>>()
    val followers: LiveData<List<Follower>> get() = _followers

    private val _followings = MutableLiveData<List<Follower>>()
    val followings: LiveData<List<Follower>> get() = _followings

    fun loadFollowers() {
        viewModelScope.launch {
            val result = repository.fetchFollowers()
            result?.let { _followers.value = it }
        }
    }

    fun loadFollowings() {
        viewModelScope.launch {
            val result = repository.fetchFollowings() // 따로 구현 필요
            result?.let { _followings.value = it }
        }
    }
}