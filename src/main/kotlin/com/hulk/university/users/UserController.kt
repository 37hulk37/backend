package com.hulk.university.users

import com.hulk.university.enums.UserType
import com.hulk.university.tables.pojos.User
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): UserInfo =
        userService.createUser(request)

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    fun updateUser(@RequestBody request: User): UserInfo =
        userService.updateUser(request)

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserInfo =
        userService.getUser(id)

    @GetMapping("/{userType}")
    fun getUsers(@PathVariable userType: UserType): List<UserInfo> =
        userService.getUsers(userType)

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): Unit =
        userService.deleteUser(id)

    @GetMapping("/{userType}")
    fun getAverageMarks(
        @PathVariable userType: UserType,
        @RequestParam from: Instant,
        @RequestParam to: Instant,
    ): List<UserStatistics> = userService.getAverageMarks(userType, from, to)

}