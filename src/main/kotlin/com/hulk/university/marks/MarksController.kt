package com.hulk.university.marks

import com.hulk.university.tables.pojos.Mark
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/marks")
@PreAuthorize("hasPermission('TEACHER')")
class MarksController(
    private val marksService: MarksService
) {
    @PostMapping
    fun createSubject(@RequestBody request: CreateMarkRequest): Mark =
        marksService.createMark(request)

    @PutMapping
    fun updateSubject(@RequestBody request: Mark): Mark =
        marksService.updateMark(request)
}