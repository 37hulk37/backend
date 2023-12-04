package com.hulk.dbkursach.people

data class CreatePeopleRequest(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val patherName: String,
    val groupId: Long
)
