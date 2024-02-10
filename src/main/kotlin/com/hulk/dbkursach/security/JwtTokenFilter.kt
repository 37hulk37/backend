package com.hulk.dbkursach.security

import com.hulk.dbkursach.security.jwt.JwtProperties
import com.hulk.dbkursach.security.jwt.JwtTokenUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtTokenFilter(
    private val jwtProperties: JwtProperties
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        val token = header.split(" ")[1].trim()

        JwtTokenUtils.getUser(token, jwtProperties)
            .map { user -> UsernamePasswordAuthenticationToken(user.username, user.password, user.authorities) }
            .ifPresent { authentication ->
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            }

        filterChain.doFilter(request, response)
    }
}