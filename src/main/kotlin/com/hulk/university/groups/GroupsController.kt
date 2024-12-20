package com.hulk.university.groups

import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.pojos.Group
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/groups")
class GroupsController(
    private val groupsService: GroupsService
) {

    @PostMapping
    fun createGroup(@RequestBody request: GroupRequest) =
        groupsService.createGroup(request.name)

    @PutMapping("/{id}")
    fun updateGroup(
        @PathVariable id: Long,
        @RequestBody request: GroupRequest
    ): Group = groupsService.updateGroup(id, request.name)

    @GetMapping
    fun getGroups() =
        groupsService.getGroups()

    @GetMapping("/{id}")
    fun getGroup(@PathVariable id: Long) =
        groupsService.getGroup(id) ?:
            throw NotFoundException("Group with id $id not exists")

    @GetMapping("/averageMarks")
    fun getAverageMarks(
        @RequestParam(required = false) from: LocalDateTime?,
        @RequestParam(required = false) to: LocalDateTime?
    ): List<GroupStatistics> {
        val actualTo = to ?: LocalDateTime.now()
        return groupsService.getAverageMarks(
            from ?: actualTo.minusYears(1),
            actualTo
        )
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(@PathVariable id: Long) =
        groupsService.deleteGroup(id)

    data class GroupRequest(
        val name: String
    )
}