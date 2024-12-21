package com.hulk.university.marks

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CreateMarkRequest(
    val studentId: Long,
    val subjectId: Long,
    val teacherId: Long,
    @Min(2) @Max(5)
    val value: Int,
    val year: Int
)
