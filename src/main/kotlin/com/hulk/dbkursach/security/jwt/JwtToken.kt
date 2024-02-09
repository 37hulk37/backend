package com.hulk.dbkursach.security.jwt

import com.hulk.dbkursach.security.CustomUser
import io.jsonwebtoken.Claims
import lombok.AllArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant
import java.util.*

@AllArgsConstructor
class JwtToken(
    private val claims: Claims
) {

    companion object {
        const val ROLES_CLAIM_NAME = "roles"
        const val SUBJECT_SPLITTER = "__"

        private val log = LoggerFactory.getLogger(JwtToken::class.java)
    }

    fun validate() =
        claims.issuedAt.before(Date.from(Instant.now()))

    fun createUser(): Optional<CustomUser> {
        return getUserData()
            .map { userData -> CustomUser(
                userData[0].toLong(),
                userData[1],
                "",
                getAuthorities()
            ) }
    }

    private fun getUserData(): Optional<List<String>> {
        val userData = claims.subject.split(SUBJECT_SPLITTER)
        if (userData.size != 2) {
            log.warn("Incorrect format of JWT: the subject is wrong");
            return Optional.empty();

        }
        return Optional.of(userData);
    }

    private fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = claims.get(ROLES_CLAIM_NAME, List::class.java) as List<*>
        if (authorities.isEmpty()) {
            log.warn("JWT token with empty list of roles for user {}", claims.subject)
        }
        return authorities.stream()
            .map { SimpleGrantedAuthority(it.toString()) }
            .toList()
    }
}