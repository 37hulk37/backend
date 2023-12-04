package com.hulk.dbkursach.marks

import com.hulk.dbkursach.tables.pojos.Mark
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/marks")
class MarksController(
    private val marksService: MarksService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreateMarkRequest
    ): Mark = marksService.createMark(request)

    @PutMapping
    fun updateSubject(
        @RequestBody request: Mark
    ): Mark = marksService.updateMark(request)
}