package com.with_runn.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.with_runn.data.course.CourseFetchRepository

class CourseDetailsVMFactory(
    private val accessToken: String,
    private val repository: CourseFetchRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseDetailsViewModel(accessToken, repository) as T
    }
}