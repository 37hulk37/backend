package com.hulk.dbkursach.USER.students

import com.hulk.dbkursach.users.CreateUserRequest
import com.hulk.dbkursach.users.UserStatistics
import com.hulk.dbkursach.tables.pojos.User
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/students")
@RestController
class StudentsController(
    private val studentsService: StudentsService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreateUserRequest
    ): StudentInfo = studentsService.createStudent(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: User
    ): StudentInfo = studentsService.updateStudent(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: Int,
        @RequestParam until: Int
    ): List<UserStatistics> =
        studentsService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        studentsService.deleteStudent(id)
}