package com.hulk.dbkursach.accounts

import com.github.dockerjava.api.exception.BadRequestException
import com.hulk.dbkursach.create
import com.hulk.dbkursach.enums.AccountType
import com.hulk.dbkursach.tables.daos.AccountDao
import com.hulk.dbkursach.tables.pojos.Account
import com.hulk.dbkursach.tables.references.ACCOUNT
import org.jooq.impl.DSL.selectFrom
import org.springframework.stereotype.Service

@Service
class AccountsService(
    private val accountDAO: AccountDao
) {
    fun createAccount(request: RegisterRequest): Account {
        if ( !isUsernameAlreadyExists(request.username)) {
            throw BadRequestException("Account with ${request.username} already exists")
        }

        //todo: validate password

        return accountDAO.create(Account(username = request.username, password = request.password, type = AccountType.User))
    }

    private fun isUsernameAlreadyExists(username: String) =
        accountDAO.ctx()
            .fetchExists(
                selectFrom(ACCOUNT)
                    .where(ACCOUNT.USERNAME.eq(username))
            )

}