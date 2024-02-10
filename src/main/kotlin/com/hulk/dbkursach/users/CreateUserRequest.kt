package com.hulk.dbkursach.users

data class CreateUserRequest(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val patherName: String,
    val groupId: Long
)
