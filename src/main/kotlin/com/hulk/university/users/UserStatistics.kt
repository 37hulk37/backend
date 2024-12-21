package com.hulk.university.users

import java.math.BigDecimal

data class UserStatistics(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val averageMark: BigDecimal
)
