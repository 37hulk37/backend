package com.hulk.university.accounts

import com.hulk.university.enums.AccountType
import com.hulk.university.tables.pojos.Account
import com.hulk.university.tables.pojos.AccountRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun Account.getAuthorities(role: AccountRole): Collection<GrantedAuthority> {
    val authorities = mutableListOf<GrantedAuthority>()
    if (type == AccountType.Admin) {
        authorities.add(SimpleGrantedAuthority(type.name))
    }
    authorities.add(SimpleGrantedAuthority(role.name))

    return authorities
}