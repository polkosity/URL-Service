package com.example.urlservice.service

import com.example.urlservice.domain.UrlEntry
import com.example.urlservice.domain.UrlRepository
import com.example.urlservice.util.IdGenerator
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory


@Service
class UrlService(private val repo: UrlRepository) {

    private val log = LoggerFactory.getLogger(UrlService::class.java)

    companion object { private const val MAX_ATTEMPTS = 5 }

    @Transactional
    fun create(url: String): UrlEntry {
        repeat(MAX_ATTEMPTS) {
            val id = IdGenerator.generateId()
            if (!repo.existsById(id)) {
                log.info("Creating short URL entry for ID: $id and URL: $url")
                return repo.save(UrlEntry(id = id, url = url))
            }
        }
        val msg = "Unable to generate unique id after $MAX_ATTEMPTS attempts"
        log.error(msg)
        throw IllegalStateException(msg)
    }

    fun get(id: String): UrlEntry {
        log.info("Getting short URL entry for ID: $id")
        return repo.findById(id).orElseThrow { EntityNotFoundException("URL not found for ID: $id") }
    }

}
