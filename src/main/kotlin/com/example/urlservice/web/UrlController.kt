package com.example.urlservice.web

import com.example.urlservice.service.UrlService
import com.example.urlservice.web.dto.UrlRequest
import com.example.urlservice.web.dto.UrlResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/short.ly")
class UrlController(private val service: UrlService) {

    @PostMapping
    fun create(@Valid @RequestBody req: UrlRequest): ResponseEntity<UrlResponse> {
        val saved = service.create(req.url)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(UrlResponse(saved.id, saved.url))
    }

    @GetMapping("/{id}/info")
    fun info(@PathVariable id: String): UrlResponse {
        val found = service.get(id)
        return UrlResponse(found.id, found.url)
    }

    @GetMapping("/{id}")
    fun redirect(@PathVariable id: String): ResponseEntity<Void> {
        val found = service.get(id)
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .location(java.net.URI.create(found.url))
            .build()
    }

}
