package com.hulk.dbkursach.people.students

import com.hulk.dbkursach.tables.pojos.People

data class StudentInfo(
    val id: Long,
    val firstName: String,
    val secondName: String,
    val patherName: String,
    val groupName: String,
) {
    constructor(student: People, groupName: String) :
        this(student.id!!, student.firstName!!, student.lastName!!, student.patherName!!, groupName)
}