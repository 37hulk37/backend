package com.hulk.university.accounts

import com.hulk.university.security.CustomUser
import com.hulk.university.security.jwt.JwtProperties
import com.hulk.university.security.jwt.generateToken
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/auth")
@RestController
class AuthController(
    private val authManager: AuthenticationManager,
    private val authService: AuthService,
    private val jwtProperties: JwtProperties,
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val user = authService.createAccount(request)
        val token = generateToken(user, jwtProperties)

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(AuthResponse(token, jwtProperties.timeToLiveInSeconds))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val authentications = authManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        val token = generateToken(authentications.principal as CustomUser, jwtProperties)

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(AuthResponse(token, jwtProperties.timeToLiveInSeconds))
    }

    data class AuthResponse(
        val token: String,
        val expiresIn: Long,
    )

    data class AuthRequest(
        val username: String,
        val password: String,
    )
}