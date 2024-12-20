package com.hulk.university.users

interface UpdateUserRequest {
    fun firstName(): String

    fun lastName(): String

    fun patherName(): String

    fun groupId(): Long? = null
}
