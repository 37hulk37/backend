package com.hulk.university.users

import com.hulk.university.tables.pojos.User

fun User.getFullName() = "$firstName $lastName $patherName"