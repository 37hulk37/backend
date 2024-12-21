package com.hulk.university.marks

import com.hulk.university.tables.pojos.Mark
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/students")
class MarksController(
    private val marksService: MarksService
) {

    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/marks")
    fun createSubject(@Valid @RequestBody request: CreateMarkRequest): Mark =
        marksService.createMark(request)

    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/marks/{id}")
    fun updateSubject(@PathVariable id: Long, @Valid @RequestBody request: UpdateMarkRequest): Mark =
        marksService.updateMark(id, request.value)

    @GetMapping("/{studentId}/marks")
    fun getMarks(@PathVariable studentId: Long) =
        marksService.getStudentMarks(studentId)

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/marks/{id}")
    fun deleteMark(@PathVariable id: Long) =
        marksService.deleteMark(id)

    data class UpdateMarkRequest(
        val value: Int
    )
}