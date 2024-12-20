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
    val id = UUID.randomUUID().toString()
    val now = Instant.now()
    return Jwts.builder()
        .id(id)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(jwtProperties.timeToLiveInSeconds)))
        .subject(customUser.username + SUBJECT_SPLITTER + customUser.id)
        .claim(ROLES_CLAIM_NAME, customUser.getUserAuthorities())
        .signWith(getSigningKey(jwtProperties.secret))
        .compact()
}

private fun parseToken(token: String, jwtProperties: JwtProperties): Claims? {
    return try {
        Jwts.parser()
            .setSigningKey(jwtProperties.secret)
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (ex: JwtException) {
        null
    }
}

private fun getSigningKey(secret: String) = Keys.hmacShaKeyFor(secret.toByteArray())
