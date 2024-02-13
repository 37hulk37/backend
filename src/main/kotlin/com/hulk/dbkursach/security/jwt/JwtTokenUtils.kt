package com.hulk.dbkursach.security.jwt

import com.hulk.dbkursach.security.CustomUser
import com.hulk.dbkursach.security.jwt.JwtProperties
import com.hulk.dbkursach.security.jwt.JwtToken.Companion.ROLES_CLAIM_NAME
import com.hulk.dbkursach.security.jwt.JwtToken.Companion.SUBJECT_SPLITTER
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import lombok.experimental.UtilityClass
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.security.Key
import java.time.Instant
import java.util.*

@UtilityClass
class JwtTokenUtils {

    companion object {

        private val signingKeys = mutableMapOf<String, Key>()

        fun getUser(token: String, jwtProperties: com.hulk.dbkursach.security.jwt.JwtProperties): Optional<CustomUser> =
            parseToken(token, jwtProperties)
                .map { JwtToken(it) }
                .filter { it.validate() }
                .flatMap { it.createUser() }

        fun generateToken(customUser: CustomUser, jwtProperties: com.hulk.dbkursach.security.jwt.JwtProperties): String {
            val id = UUID.randomUUID().toString()
            val now = Instant.now()
            return Jwts.builder()
                .setId(id)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtProperties.timeToLiveInSeconds)))
                .setSubject(customUser.username + SUBJECT_SPLITTER + customUser.id)
                .claim(ROLES_CLAIM_NAME, customUser.getUserAuthorities())
                .signWith(getSigningKey(jwtProperties.secret), SignatureAlgorithm.HS256)
                .compact()
        }

        private fun parseToken(token: String, jwtProperties: com.hulk.dbkursach.security.jwt.JwtProperties): Optional<Claims> =
            try {
                Optional.of(
                    Jwts
                        .parserBuilder()
                        .setSigningKey(jwtProperties.secret)
                        .build()
                        .parseClaimsJwt(token)
                        .body
                )
            } catch (ex: JwtException) {
                Optional.empty()
            }

        private fun getSigningKey(secret: String) =
            signingKeys.computeIfAbsent(secret) { key -> Keys.hmacShaKeyFor(key.toByteArray()) }
    }

}