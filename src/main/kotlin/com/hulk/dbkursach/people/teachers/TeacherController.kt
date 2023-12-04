package com.hulk.dbkursach.people.teachers

import com.hulk.dbkursach.people.CreatePeopleRequest
import com.hulk.dbkursach.people.PeopleInfo
import com.hulk.dbkursach.people.students.StudentInfo
import com.hulk.dbkursach.people.PeopleStatistics
import com.hulk.dbkursach.tables.pojos.People
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/teachers")
class TeacherController(
    private val teachersService: TeachersService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreatePeopleRequest
    ): TeacherInfo = teachersService.createTeacher(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: People
    ): TeacherInfo = teachersService.updateTeacher(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: Int,
        @RequestParam until: Int
    ): List<PeopleStatistics> = teachersService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        teachersService.deleteTeacher(id)
}