package com.hulk.university.auth

import com.hulk.university.tables.pojos.Account
import com.hulk.university.tables.pojos.AccountRole
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun Account.getAuthorities(role: AccountRole? = null): Collection<SimpleGrantedAuthority> {
    val authorities = mutableListOf<SimpleGrantedAuthority>()
    if (isAdmin!!) {
        authorities.add(SimpleGrantedAuthority("ADMIN"))
    }
    role?.let {
        authorities.add(SimpleGrantedAuthority(it.name!!.uppercase()))
    }

    return authorities
}