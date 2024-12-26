package com.hulk.university.config

import com.hulk.university.auth.SystemUserProperties
import com.hulk.university.security.JwtTokenFilter
import com.hulk.university.security.SystemUserAuthenticationProvider
import com.hulk.university.security.jwt.JwtProperties
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableConfigurationProperties(value = [
    JwtProperties::class,
    SystemUserProperties::class
])
@EnableMethodSecurity(securedEnabled = true)
@Configuration
class SecurityConfiguration(
    private val systemUserProperties: SystemUserProperties
) {

    private val log = LoggerFactory.getLogger(SecurityConfiguration::class.java)!!

    @Bean
    fun securityFilterChain(
        jwtFilter: JwtTokenFilter,
        http: HttpSecurity,
        authenticationEntryPoint: AuthenticationEntryPoint
    ): SecurityFilterChain =
        http
            .cors(Customizer.withDefaults())
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { requests -> requests
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/students/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/groups/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/subjects/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                .anyRequest().authenticated()
            }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { config -> config.authenticationEntryPoint(authenticationEntryPoint) }
            .build()

    @Bean
    fun authenticationEntryPoint() = AuthenticationEntryPoint { _, response, authException ->
        log.error("Authentication entry point failed: {0}", authException)

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
    }

    @Bean
    fun authManager(
        http: HttpSecurity,
        userDetailsService: UserDetailsService,
        systemUserAuthenticationProvider: SystemUserAuthenticationProvider?,
    ): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.userDetailsService(userDetailsService)
        systemUserAuthenticationProvider?.let(authenticationManagerBuilder::authenticationProvider)

        return authenticationManagerBuilder.build()
    }

    @Bean
    @ConditionalOnProperty(name = ["app.system-user.enabled"], havingValue = "true")
    fun systemUserAuthenticationProvider(passwordEncoder: PasswordEncoder): SystemUserAuthenticationProvider {
        val provider = SystemUserAuthenticationProvider(
            systemUserProperties.id,
            systemUserProperties.username,
            systemUserProperties.password,
            passwordEncoder
        )
        provider.isHideUserNotFoundExceptions = false

        return provider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}