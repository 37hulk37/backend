package com.hulk.dbkursach.USER.students

import com.hulk.dbkursach.tables.pojos.User

data class StudentInfo(
    val id: Long,
    val fullname: String,
    val groupName: String,
) {
    constructor(student: User, groupName: String) :
        this(student.id!!, student.firstName!! + student.lastName!! + student.patherName!!, groupName)
}