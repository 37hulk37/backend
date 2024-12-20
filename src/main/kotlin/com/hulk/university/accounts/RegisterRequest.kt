package com.hulk.university.accounts

import com.hulk.university.enums.UserType

data class RegisterRequest(
    val username: String,
    val password: String,
    val isAdmin: Boolean,
    val userType: UserType
)
