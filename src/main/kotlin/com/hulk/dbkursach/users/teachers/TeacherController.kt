package com.hulk.dbkursach.users.teachers

import com.hulk.dbkursach.users.CreateUserRequest
import com.hulk.dbkursach.users.UserStatistics
import com.hulk.dbkursach.tables.pojos.User
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/teachers")
class TeacherController(
    private val teachersService: TeachersService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreateUserRequest
    ): TeacherInfo = teachersService.createTeacher(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: User
    ): TeacherInfo = teachersService.updateTeacher(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: @PositiveOrZero Int,
        @RequestParam until: @PositiveOrZero Int
    ): List<UserStatistics> = teachersService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        teachersService.deleteTeacher(id)
}