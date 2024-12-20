package com.hulk.university.users

import com.hulk.university.enums.UserType
import java.time.Instant

interface UserHandler {

    fun getUserType(): UserType

    fun getAverageMarks(from: Instant, to: Instant): List<UserStatistics>

    fun getGroupName(groupId: Long?): String = ""
}