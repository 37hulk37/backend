package com.hulk.university.users

import com.hulk.university.tables.pojos.User

data class UserInfo(
    val id: Long,
    val fullName: String,
    val groupId: Long,
    val groupName: String,
) {
    constructor(student: User, groupName: String):
        this(student.id!!, student.getFullName(), student.groupId!!, groupName)
}
