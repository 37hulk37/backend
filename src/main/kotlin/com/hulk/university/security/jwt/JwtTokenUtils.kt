package com.hulk.university.security.jwt

import com.hulk.university.security.CustomUser
import com.hulk.university.security.jwt.JwtToken.Companion.ROLES_CLAIM_NAME
import com.hulk.university.security.jwt.JwtToken.Companion.SUBJECT_SPLITTER
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.util.*

fun getUser(token: String, jwtProperties: JwtProperties): CustomUser? =
    parseToken(token, jwtProperties)
        ?.let { JwtToken(it) }
        ?.takeIf { it.validate() }
        ?.createUser()


fun generateToken(customUser: CustomUser, jwtProperties: JwtProperties): String {
    val now = Instant.now()
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(jwtProperties.timeToLiveInSeconds)))
        .subject("${customUser.id}$SUBJECT_SPLITTER${customUser.username}")
        .claim(ROLES_CLAIM_NAME, customUser.getUserAuthorities())
        .signWith(getSigningKey(jwtProperties.secret), Jwts.SIG.HS256)
        .compact()
}

private fun parseToken(token: String, jwtProperties: JwtProperties): Claims? {
    return try {
        Jwts.parser()
            .verifyWith(getSigningKey(jwtProperties.secret))
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (ex: JwtException) {
        null
    }
}

private fun getSigningKey(secret: String) = Keys.hmacShaKeyFor(secret.toByteArray())
