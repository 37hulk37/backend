package com.hulk.university.users

import com.hulk.university.enums.UserType
import java.time.LocalDateTime

interface UserHandler {

    fun getUserType(): UserType

    fun getAverageMarks(from: LocalDateTime, to: LocalDateTime): List<UserStatistics>
}