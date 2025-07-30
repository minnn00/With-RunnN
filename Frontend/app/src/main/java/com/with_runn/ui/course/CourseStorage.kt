package com.with_runn.ui.course

import com.with_runn.data.WalkCourse

object CourseStorage {
    val scrapList = mutableListOf<WalkCourse>()  // 스크랩한 코스 목록
    val likeList = mutableListOf<WalkCourse>()   // 좋아요한 코스 목록

    fun addScrap(course: WalkCourse) {
        if (!scrapList.contains(course)) {
            scrapList.add(course)
        }
    }

    fun addLike(course: WalkCourse) {
        if (!likeList.contains(course)) {
            likeList.add(course)
        }
    }

    // 필요하다면 제거도 가능하게:
    fun removeScrap(course: WalkCourse) {
        scrapList.remove(course)
    }

    fun removeLike(course: WalkCourse) {
        likeList.remove(course)
    }
}
