package com.with_runn.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.with_runn.data.course.CourseFetchRepository
import com.with_runn.data.course.CourseActionRepository

class CourseDetailsVMFactory(
    private val accessToken: String,
    private val fetchRepository: CourseFetchRepository,
    private val actionRepository: CourseActionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseDetailsViewModel(
            accessToken = accessToken,
            fetchRepository = fetchRepository,
            actionRepository = actionRepository
        ) as T
    }
}