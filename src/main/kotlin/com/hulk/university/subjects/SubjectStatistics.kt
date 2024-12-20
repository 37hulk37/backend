package com.hulk.university.subjects

import java.math.BigDecimal

data class SubjectStatistics(
    val id: Long,
    val name: String,
    val averageMark: BigDecimal
)