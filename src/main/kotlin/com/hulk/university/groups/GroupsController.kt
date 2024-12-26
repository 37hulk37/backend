package com.hulk.university.groups

import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.pojos.Group
import com.hulk.university.users.minusYears
import org.springframework.web.bind.annotation.*
import java.time.Instant

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
        @RequestParam(required = false) from: Instant?,
        @RequestParam(required = false) to: Instant?
    ): List<GroupStatistics> {
        val actualTo = to ?: Instant.now()
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