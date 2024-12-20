package com.hulk.university.users.teachers

import com.hulk.university.enums.UserType
import com.hulk.university.users.CreateUserRequest

data class CreateTeacherRequest(
    val firstName: String,
    val lastName: String,
    val patherName: String,
): CreateUserRequest {

    override fun firstName(): String = firstName

    override fun lastName(): String = lastName

    override fun patherName(): String  = patherName

    override fun userType(): UserType = UserType.Teacher
}
