package com.hulk.university.users

import com.hulk.university.tables.pojos.User
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun User.getFullName() = "$firstName $lastName $patherName"

fun Instant.minusYears(amount: Long): Instant {
    val zone = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(this, zone)
        .minusYears(amount)
        .atZone(zone).toInstant()
}