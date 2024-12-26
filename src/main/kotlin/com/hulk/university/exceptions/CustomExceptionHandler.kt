package com.hulk.university.exceptions

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@ControllerAdvice
class CustomExceptionHandler(
    private val objectMapper: ObjectMapper
): ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(CustomExceptionHandler::class.java)!!

    @ExceptionHandler(Throwable::class)
    fun handleError(request: WebRequest, t: Throwable): ResponseEntity<Any>? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        log.error("Error while processing request", t)
        return handleExceptionInternal(
            RuntimeException(t),
            objectMapper.writeValueAsString(getBody(t, request)),
            headers,
            getStatus(t),
            request
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? = handleError(request, ex)

    private fun getBody(t: Throwable, request: WebRequest): Error {
        val sj = StringJoiner(", ")
        sj.add(t.message)

        if (t is MethodArgumentNotValidException) {
            t.allErrors.forEach { error ->
                sj.add("Field with ${error.objectName} has wrong value ${error.defaultMessage}")
            }
        }

        return Error(
            title = t.javaClass.simpleName,
            details = sj.toString(),
            requestInfo = request.getDescription(false)
        )
    }

    private fun getStatus(t: Throwable): HttpStatus {
        return when(t) {
            is NotFoundException -> HttpStatus.NOT_FOUND
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            is UsernameNotFoundException -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.BAD_REQUEST
        }
    }

    data class Error(
        val title: String,
        val details: String,
        val requestInfo: String,
    )
}