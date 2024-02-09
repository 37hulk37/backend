package com.hulk.dbkursach.accounts

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/auth")
@RestController
@RequiredArgsConstructor
class AuthController(

) {
    @PostMapping("/login")
    fun login(): ResponseEntity<Any> {
        TODO()
    }

    @PostMapping("/register")
    fun register(): ResponseEntity<Any> {
        TODO()
    }
}