package com.hulk.university.users.teachers

import com.hulk.university.users.UserInfo
import com.hulk.university.users.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/teachers")
@RestController
class TeacherController(
    private val userService: UserService,
) {
    @PostMapping
    fun createTeacher(@RequestBody request: CreateTeacherRequest): UserInfo =
        userService.createUser(request)

    @PutMapping("/{id}")
    fun updateTeacher(@PathVariable id: Long, @RequestBody request: UpdateTeacherRequest): UserInfo =
        userService.updateUser(id, request)
}