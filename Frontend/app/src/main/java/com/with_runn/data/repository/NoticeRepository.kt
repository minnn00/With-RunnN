package com.with_runn.data.repository

import com.with_runn.data.model.Notice
import com.with_runn.data.network.ApiService

class NoticeRepository(private val api: ApiService) {
    suspend fun fetchNotices(): List<Notice> {
        return api.getNotices()
    }
}
