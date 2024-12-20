package com.hulk.university.users.students

import com.hulk.university.users.UserInfo
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/students")
@RestController
class StudentController(
    private val studentProvider: StudentHandler,
) {
    @PostMapping
    fun createSubject(@RequestBody request: CreateStudentRequest): UserInfo =
        studentProvider.createUser(request)
}