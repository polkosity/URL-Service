package com.example.urlservice.web.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class UrlRequest(
    @field:NotBlank(message = "url must not be blank")
    @field:URL(message = "Must be a valid http or https URL")
    val url: String
)

data class UrlResponse(
    val id: String,
    val url: String
)
