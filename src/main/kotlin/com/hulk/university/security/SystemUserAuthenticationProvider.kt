package com.hulk.university.security

import com.hulk.university.enums.AccountType
import org.springframework.security.authentication.*
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.*
import org.springframework.security.crypto.password.PasswordEncoder

class SystemUserAuthenticationProvider(
    private val id: Long,
    private val username: String,
    private val password: String,
    private val passwordEncoder: PasswordEncoder
): AbstractUserDetailsAuthenticationProvider() {

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails?,
        authentication: UsernamePasswordAuthenticationToken?
    ) {
    }

    override fun retrieveUser(username: String?, authentication: UsernamePasswordAuthenticationToken?): UserDetails {
        if (this.username != username) {
            throw UsernameNotFoundException("Current user is not system one")
        }
        val presentedPassword = authentication!!.credentials as String

        if (!passwordEncoder.matches(presentedPassword, password)) {
            val errorMessage = messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"
            )
            throw BadCredentialsException(errorMessage)
        }

        return CustomUser(
            id,
            username,
            presentedPassword,
            listOf(SimpleGrantedAuthority(AccountType.Admin.name))
        )
    }
}