package com.hulk.dbkursach.people.students

import com.hulk.dbkursach.people.CreatePeopleRequest
import com.hulk.dbkursach.people.PeopleStatistics
import com.hulk.dbkursach.tables.pojos.People
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/students")
@RestController
class StudentsController(
    private val studentsService: StudentsService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreatePeopleRequest
    ): StudentInfo = studentsService.createStudent(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: People
    ): StudentInfo = studentsService.updateStudent(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: Int,
        @RequestParam until: Int
    ): List<PeopleStatistics> =
        studentsService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        studentsService.deleteStudent(id)
}