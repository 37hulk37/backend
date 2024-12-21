package com.hulk.university.users.students

import com.hulk.university.users.UpdateUserRequest

data class UpdateStudentRequest(
    val firstName: String,
    val lastName: String,
    val patherName: String,
    val groupId: Long
): UpdateUserRequest {

    override fun firstName(): String = firstName

    override fun lastName(): String = lastName

    override fun patherName(): String = patherName

    override fun groupId(): Long = groupId
}
