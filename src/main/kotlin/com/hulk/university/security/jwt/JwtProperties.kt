package com.hulk.university.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.security.jwt")
data class JwtProperties(
    val secret: String,
    val timeToLiveInSeconds: Long
)