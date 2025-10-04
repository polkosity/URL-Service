package com.example.urlservice.web

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {

    data class ApiError(
        val timestamp: Instant = Instant.now(),
        val status: Int,
        val error: String,
        val message: String?,
        val fieldErrors: Map<String, String?>? = null
    )

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiError(status = 404, error = "Not Found", message = ex.message)
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val fields = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage) }
        val body = ApiError(
            status = 400,
            error = "Bad Request",
            message = "Validation failed",
            fieldErrors = fields
        )
        return ResponseEntity.badRequest().body(body)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException) =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiError(status = 500, error = "Internal Server Error", message = ex.message)
        )
}
