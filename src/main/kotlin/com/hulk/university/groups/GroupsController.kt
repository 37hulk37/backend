package com.hulk.university.groups

import com.hulk.university.tables.pojos.Group
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/groups")
class GroupsController(
    private val groupsService: GroupsService
) {
    @PostMapping
    fun createSubject(
        @RequestBody request: CreateGroupRequest
    ): Group = groupsService.createGroup(request.name)

    @PutMapping
    fun updateSubject(
        @RequestBody request: Group
    ): Group = groupsService.updateGroup(request)

    @GetMapping
    fun getAverageMarks(
        @RequestParam from: Int,
        @RequestParam until: Int
    ): List<GroupStatistics> =
        groupsService.getAverageMarks(from, until)

    @DeleteMapping("/{id}")
    fun deleteSubject(@PathVariable id: Long): Unit =
        groupsService.deleteGroup(id)

    data class CreateGroupRequest(
        val name: String
    )
}