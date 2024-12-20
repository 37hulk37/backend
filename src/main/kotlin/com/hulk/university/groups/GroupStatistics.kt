package com.hulk.university.groups

import java.math.BigDecimal

data class GroupStatistics(
    val id: Long,
    val name: String,
    val averageMark: BigDecimal
)
