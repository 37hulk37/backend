package com.hulk.dbkursach.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetails

class SystemUserAuthenticationProvider(
    private val id: Long,
    private val username: String,
    private val password: String,
): AbstractUserDetailsAuthenticationProvider() {

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails?,
        authentication: UsernamePasswordAuthenticationToken?
    ) {
        TODO("Not yet implemented")
    }

    override fun retrieveUser(username: String?, authentication: UsernamePasswordAuthenticationToken?): UserDetails {
        TODO("Not yet implemented")
    }
}