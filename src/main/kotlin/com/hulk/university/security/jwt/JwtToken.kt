package com.hulk.university.security.jwt

import com.hulk.university.security.CustomUser
import io.jsonwebtoken.Claims
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant
import java.util.*

class JwtToken(
    private val claims: Claims
) {

    companion object {
        const val ROLES_CLAIM_NAME = "roles"
        const val SUBJECT_SPLITTER = "__"
    }

    fun validate() = claims.issuedAt
        .before(Date.from(Instant.now()))

    fun createUser(): CustomUser? =
        getUserData()
            ?.let { userData -> CustomUser(
                userData[0].toLong(),
                userData[1],
                "",
                getAuthorities()
            ) }

    private fun getUserData(): List<String>? {
        val userData = claims.subject.split(SUBJECT_SPLITTER)
        if (userData.size != 2) {
            return null
        }
        return userData
    }

    private fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = claims.get(ROLES_CLAIM_NAME, List::class.java) as List<*>

        return authorities.stream()
            .map { SimpleGrantedAuthority(it.toString()) }
            .toList()
    }

}