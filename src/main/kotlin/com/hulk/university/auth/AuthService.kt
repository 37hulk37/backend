package com.hulk.university.auth

import com.hulk.university.create
import com.hulk.university.exceptions.ApplicationException
import com.hulk.university.security.CustomUser
import com.hulk.university.tables.daos.AccountDao
import com.hulk.university.tables.daos.AccountRoleDao
import com.hulk.university.tables.pojos.Account
import com.hulk.university.tables.pojos.AccountRole
import com.hulk.university.tables.references.ACCOUNT
import org.jooq.impl.DSL.selectFrom
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

@Component
class AuthService(
    private val accountDao: AccountDao,
    private val accountRoleDao: AccountRoleDao,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
     fun createAccount(request: RegisterRequest): CustomUser {
        if (isUsernameAlreadyExists(request.username)) {
            throw ApplicationException("Account with ${request.username} already exists")
        }
        val account = accountDao.create(Account(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            isAdmin = request.isAdmin
        ))
        val roleName = request.getRoleNameOrNull()
        if (!StringUtils.hasText(roleName)) {
            return CustomUser(account, account.getAuthorities())
        }

        val role = accountRoleDao.create(AccountRole(
            accountId = account.id,
            name = roleName,
        ))
        return CustomUser(account, account.getAuthorities(role))
    }

    private fun isUsernameAlreadyExists(username: String) = accountDao.ctx()
        .fetchExists(
            selectFrom(ACCOUNT)
                .where(ACCOUNT.USERNAME.eq(username))
        )

}