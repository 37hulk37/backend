package com.hulk.dbkursach.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.security.jwt")
data class JwtProperties(
    val secret: String,
    val timeToLiveInSeconds: Long
)