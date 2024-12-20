package com.hulk.university.subjects

import com.hulk.university.tables.pojos.Subject
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@Secured("ADMIN")
@RequestMapping("/api/subjects")
class SubjectController(
    private val subjectService: SubjectService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreateSubjectRequest
    ): Subject = subjectService.createSubject(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: Subject
    ): Subject = subjectService.updateSubject(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: Int,
        @RequestParam until: Int
    ): List<SubjectStatistics> =
        subjectService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        subjectService.deleteSubject(id)
}