package com.hulk.university.users

import com.hulk.university.enums.UserType

interface CreateUserRequest {

    fun firstName(): String

    fun lastName(): String

    fun patherName(): String

    fun groupId(): Long? = null

    fun userType(): UserType

}
