package com.hulk.university.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hulk.university.enums.UserType

data class RegisterRequest(
    val username: String,
    val password: String,
    val isAdmin: Boolean,
    val userType: String
) {

    @JsonIgnore
    fun getRoleNameOrNull(): String? =
        UserType.values()
            .firstOrNull { it.name == userType }
            ?.name

}
