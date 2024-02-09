package com.hulk.dbkursach.security

import com.hulk.dbkursach.enums.AccountType
import com.hulk.dbkursach.tables.daos.AccountDao
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserDetailsServiceImpl(
    private val accountDao: AccountDao
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val account = accountDao.fetchOneByUsername(username!!)
            ?: throw UsernameNotFoundException("Account with username $username not found")

        return CustomUser(account, getAuthorities(account.type!!))
    }

    private fun getAuthorities(type: AccountType): Collection<GrantedAuthority> {
        val authorities = arrayListOf<GrantedAuthority>()
        if (Objects.equals(type, AccountType.Admin)) {
            authorities.add(SimpleGrantedAuthority(AccountType.Admin.name))
        }
        return authorities
    }
}