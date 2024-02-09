package com.hulk.dbkursach.security

import com.hulk.dbkursach.tables.pojos.Account
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUser(
    val id: Long,
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>
): User(username, password, authorities) {

    constructor(account: Account, authorities: Collection<GrantedAuthority>):
            this(account.id!!, account.username!!, account.password!!, authorities)

    fun getUserAuthorities(): List<String> =
        authorities.stream()
            .map { it.authority }
            .toList()
}
