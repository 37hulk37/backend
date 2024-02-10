package com.hulk.dbkursach.accounts

import com.hulk.dbkursach.create
import com.hulk.dbkursach.enums.AccountType
import com.hulk.dbkursach.security.CustomUser
import com.hulk.dbkursach.security.jwt.JwtProperties
import com.hulk.dbkursach.security.jwt.JwtTokenUtils
import com.hulk.dbkursach.tables.daos.AccountDao
import com.hulk.dbkursach.tables.pojos.Account
import com.hulk.dbkursach.tables.references.ACCOUNT
import lombok.RequiredArgsConstructor
import org.jooq.impl.DSL.selectFrom
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
@RequiredArgsConstructor
class AuthController(
    private val authManager: AuthenticationManager,
    private val accountDao: AccountDao,
    private val jwtProperties: JwtProperties,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val user = authenticateUser(request.username, request.password)
        val token = JwtTokenUtils.generateToken(user, jwtProperties)

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(AuthResponse(token))
    }

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val user = createAccount(request.username, request.password)
        val token = JwtTokenUtils.generateToken(user, jwtProperties)

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, token)
            .body(AuthResponse(token))
    }

    private fun createAccount(username: String, password: String): CustomUser {
        if (isUsernameAlreadyExists(username)) {
            throw RuntimeException("Account with $username already exists")
        }
        val account = accountDao.create(Account(
            username = username,
            password = password,
            type = AccountType.User
        ))

        return CustomUser(account, emptyList())
    }

    private fun authenticateUser(username: String, password: String): CustomUser {
        val authentication = authManager.authenticate(UsernamePasswordAuthenticationToken(username, password))

        return authentication.principal as CustomUser
    }

    private fun isUsernameAlreadyExists(username: String) =
        accountDao.ctx()
            .fetchExists(
                selectFrom(ACCOUNT)
                    .where(ACCOUNT.USERNAME.eq(username))
            )


    data class AuthResponse(
        val token: String
    )

    data class AuthRequest(
        val username: String,
        val password: String,
    )
}