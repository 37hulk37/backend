package com.hulk.university.users.students

import com.hulk.university.enums.UserType
import com.hulk.university.users.CreateUserRequest

data class CreateStudentRequest(
    val firstName: String,
    val lastName: String,
    val patherName: String,
    val groupId: Long,
): CreateUserRequest {

    override fun firstName(): String = firstName

    override fun lastName(): String = lastName

    override fun patherName(): String = patherName

    override fun groupId(): Long = groupId

    override fun userType(): UserType = UserType.Student
}