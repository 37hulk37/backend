package com.hulk.university.accounts

import com.hulk.university.create
import com.hulk.university.enums.AccountType
import com.hulk.university.exceptions.ApplicationException
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.security.CustomUser
import com.hulk.university.tables.daos.AccountDao
import com.hulk.university.tables.daos.AccountRoleDao
import com.hulk.university.tables.pojos.Account
import com.hulk.university.tables.pojos.AccountRole
import com.hulk.university.tables.references.ACCOUNT
import org.jooq.impl.DSL.selectFrom
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AuthService(
    private val accountDao: AccountDao,
    private val accountRoleDao: AccountRoleDao,
) {

    @Transactional
     fun createAccount(request: RegisterRequest): CustomUser {
        if (isUsernameAlreadyExists(request.username)) {
            throw ApplicationException("Account with ${request.username} already exists")
        }
        val account = accountDao.create(Account(
            username = request.username,
            password = request.password,
            type = if (request.isAdmin) AccountType.Admin else AccountType.User
        ))
        val role = accountRoleDao.create(AccountRole(
            accountId = account.id,
            name = request.userType.name
        ))

        return CustomUser(account, account.getAuthorities(role))
    }

    private fun isUsernameAlreadyExists(username: String) =
        accountDao.ctx()
            .fetchExists(
                selectFrom(ACCOUNT)
                    .where(ACCOUNT.USERNAME.eq(username))
            )

}