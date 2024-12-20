package com.hulk.university.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomExceptionHandler(
    private val objectMapper: ObjectMapper
): ResponseEntityExceptionHandler() {

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(request: WebRequest, t: Throwable): ResponseEntity<Any>? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        return handleExceptionInternal(
            RuntimeException(t),
            objectMapper.writeValueAsString(ErrorInfo(t.message)),
            headers,
            getStatus(t),
            request
        )
    }

    private fun getStatus(t: Throwable): HttpStatus {
        if (t is NotFoundException) {
            return HttpStatus.NOT_FOUND
        }
        if (t is UsernameNotFoundException) {
            return HttpStatus.UNAUTHORIZED
        }
        return HttpStatus.BAD_REQUEST
    }

    data class ErrorInfo(
        val message: String?,
    )
}