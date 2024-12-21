package com.hulk.university.users.students

import com.hulk.university.users.UserInfo
import com.hulk.university.users.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/students")
@RestController
class StudentController(
    private val userService: UserService,
) {
    @PostMapping
    fun createStudent(@RequestBody request: CreateStudentRequest): UserInfo =
        userService.createUser(request)

    @PutMapping("/{id}")
    fun updateStudent(@PathVariable id: Long, @RequestBody request: UpdateStudentRequest): UserInfo =
        userService.updateUser(id, request)
}