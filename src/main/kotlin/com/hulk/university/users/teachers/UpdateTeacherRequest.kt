package com.hulk.university.users.teachers

import com.hulk.university.users.UpdateUserRequest

data class UpdateTeacherRequest(
    val firstName: String,
    val lastName: String,
    val patherName: String,
): UpdateUserRequest {

    override fun firstName(): String = firstName

    override fun lastName(): String = lastName

    override fun patherName(): String = patherName
}
