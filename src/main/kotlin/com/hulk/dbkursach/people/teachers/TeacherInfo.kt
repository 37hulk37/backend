package com.hulk.dbkursach.people.teachers

import com.hulk.dbkursach.tables.pojos.People

data class TeacherInfo(
    val id: Long,
    val firstName: String,
    val secondName: String,
    val patherName: String
) {
    constructor(teacher: People):
            this(teacher.id!!, teacher.firstName!!, teacher.lastName!!, teacher.patherName!!)
}
