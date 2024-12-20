package com.hulk.university.users

import com.hulk.university.enums.UserType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserInfo =
        userService.getUser(id)

    @GetMapping
    fun getUsers(@RequestParam userType: UserType): List<UserInfo> =
        userService.getUsers(userType)

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): Unit =
        userService.deleteUser(id)

    @GetMapping("/{userType}/averageMarks")
    fun getAverageMarks(
        @PathVariable userType: UserType,
        @RequestParam(required = false) from: LocalDateTime?,
        @RequestParam(required = false) to: LocalDateTime?,
    ): List<UserStatistics> {
        val actualTo = to ?: LocalDateTime.now()
        return userService.getAverageMarks(
            userType,
            from ?: actualTo.minusYears(1),
            actualTo
        )
    }

}