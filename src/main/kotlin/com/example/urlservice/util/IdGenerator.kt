package com.example.urlservice.util

import java.security.SecureRandom
import java.util.Base64

object IdGenerator {
    private val secureRandom = SecureRandom()
    private val encoder = Base64.getUrlEncoder().withoutPadding()

    /**
     * Generate a 32-bit random ID, URL-safe Base64 encoded with no padding
     * e.g. a1B2c3
     */
    fun generateId(): String {
        val bytes = ByteArray(4)
        secureRandom.nextBytes(bytes)
        return encoder.encodeToString(bytes)
    }
}
