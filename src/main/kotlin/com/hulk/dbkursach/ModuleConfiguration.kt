package com.hulk.dbkursach

import com.hulk.dbkursach.accounts.SystemUserProperties
import com.hulk.dbkursach.security.JwtTokenFilter
import com.hulk.dbkursach.security.SystemUserAuthenticationProvider
import com.hulk.dbkursach.security.jwt.JwtProperties
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*

@EnableWebSecurity
@EnableConfigurationProperties(value = [
    JwtProperties::class,
    SystemUserProperties::class
])
@Configuration
class ModuleConfiguration(
    private val systemUserProperties: SystemUserProperties
) {

    @Bean
    fun securityFilterChain(
        jwtFilter: JwtTokenFilter,
        http: HttpSecurity,
        authenticationEntryPoint: AuthenticationEntryPoint
    ): SecurityFilterChain =
        http
            .securityContext { context -> context.requireExplicitSave(false) }
            .cors(Customizer.withDefaults())
            .csrf(Customizer.withDefaults())
            .securityMatchers {matchers -> matchers.requestMatchers("/api/**") }
            .authorizeHttpRequests { requests -> requests.anyRequest().authenticated() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { config -> config.authenticationEntryPoint(authenticationEntryPoint) }
            .build()

    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint::class)
    fun authenticationEntryPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { _, response, authException ->
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                authException.message
            )
        }

    @Bean
    fun authManager(
        http: HttpSecurity,
        userDetailsService: UserDetailsService,
        systemUserAuthenticationProvider: Optional<SystemUserAuthenticationProvider>,
    ): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.userDetailsService(userDetailsService)
        systemUserAuthenticationProvider.ifPresent(authenticationManagerBuilder::authenticationProvider)

        return authenticationManagerBuilder.build()
    }

    @Bean
    @ConditionalOnProperty(name = ["app.system-user.enabled"], havingValue = "true")
    fun systemUserAuthenticationProvider(): SystemUserAuthenticationProvider {
        val provider = SystemUserAuthenticationProvider(
            systemUserProperties.id,
            systemUserProperties.username,
            systemUserProperties.password,
            passwordEncoder()
        )
        provider.isHideUserNotFoundExceptions = false

        return provider
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers("/swagger-ui*")
                .requestMatchers("/swagger-ui/*")
                .requestMatchers("/api-docs*")
                .requestMatchers("/api-docs/*")
        }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}