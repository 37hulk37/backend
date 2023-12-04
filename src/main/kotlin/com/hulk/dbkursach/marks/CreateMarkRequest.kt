package com.hulk.dbkursach.marks

data class CreateMarkRequest(
    val studentId: Long,
    val subjectId: Long,
    val teacherId: Long,
    val value: Int,
    val year: Int
)
