package com.hulk.dbkursach.marks

import com.hulk.dbkursach.people.students.StudentInfo
import com.hulk.dbkursach.people.teachers.TeacherInfo
import com.hulk.dbkursach.tables.pojos.Subject

data class MarkInfo(
    val id: Long,
    val student: StudentInfo,
    val subject: Subject,
    val teacher: TeacherInfo
)
