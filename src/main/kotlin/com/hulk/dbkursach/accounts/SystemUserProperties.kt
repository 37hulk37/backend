package com.hulk.dbkursach.accounts

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.system-user")
data class SystemUserProperties(
    val enabled: Boolean,
    val id: Long,
    val username: String,
    val password: String,
)
