package com.hulk.dbkursach.users.teachers

import com.hulk.dbkursach.tables.pojos.User

data class TeacherInfo(
    val id: Long,
    val fullname: String,
) {
    constructor(teacher: User):
            this(teacher.id!!, teacher.firstName + teacher.lastName!! + teacher.patherName!!)
}
