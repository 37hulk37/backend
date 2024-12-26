package com.hulk.university

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SecurityScheme(
    name = "JWT",
    type = SecuritySchemeType.APIKEY,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@EnableScheduling
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
