package com.hulk.university.subjects

import com.hulk.university.tables.pojos.Subject
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@Secured("ADMIN")
@RequestMapping("/api/subjects")
class SubjectController(
    private val subjectService: SubjectService
) {
    @PostMapping
    fun createSubject(@RequestBody request: SubjectRequest): Subject =
        subjectService.createSubject(request.name)

    @PutMapping("/{id}")
    fun updateSubject(
        @PathVariable id: Long,
        @RequestBody request: SubjectRequest
    ): Subject = subjectService.updateSubject(id, request.name)

    @GetMapping
    fun getSubjects() =
        subjectService.getSubjects()

    @GetMapping("/{id}")
    fun getSubject(@PathVariable id: Long) =
        subjectService.getSubject(id)

    @GetMapping("/averageMarks")
    fun getAverageMarks(
        @RequestParam from: LocalDateTime?,
        @RequestParam to: LocalDateTime?
    ): List<SubjectStatistics> {
        val actualTo = to ?: LocalDateTime.now()
        return subjectService.getAverageMarks(
            from ?: actualTo.minusYears(1),
            actualTo
        )
    }

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        subjectService.deleteSubject(id)

    data class SubjectRequest(
        val name: String
    )
}