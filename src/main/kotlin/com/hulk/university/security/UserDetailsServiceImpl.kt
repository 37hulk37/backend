package com.hulk.university.security

import com.hulk.university.accounts.getAuthorities
import com.hulk.university.exceptions.NotFoundException
import com.hulk.university.tables.AccountRole.Companion.ACCOUNT_ROLE
import com.hulk.university.tables.daos.AccountDao
import com.hulk.university.tables.daos.AccountRoleDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserDetailsServiceImpl(
    private val accountDao: AccountDao,
    private val accountRoleDao: AccountRoleDao
): UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String?): UserDetails {
        val account = accountDao.fetchOneByUsername(username!!)
            ?: throw UsernameNotFoundException("Account with username $username not found")

        val role = accountRoleDao.fetchOne(ACCOUNT_ROLE.ACCOUNT_ID, account.id)
            ?: throw NotFoundException("Role for account ${account.id} not found")

        return CustomUser(account, account.getAuthorities(role))
    }
}