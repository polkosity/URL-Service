package com.example.urlservice.service

import com.example.urlservice.domain.UrlEntry
import com.example.urlservice.domain.UrlRepository
import com.example.urlservice.util.IdGenerator
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

class UrlServiceTest {
    private val repo: UrlRepository = mockk(relaxed = true)
    private val service = UrlService(repo)

    @Test
    fun `get valid id returns valid entity`() {
        val id = "a1B2c3"
        val url = "www.originenergy.com.au/electricity-gas/plans.html"
        every { repo.findById(any()) } returns Optional.of(UrlEntry(id = id, url = url))
        val saved = service.get(id)
        verify { repo.findById(id) }
        assertNotNull(saved)
        assertEquals(saved.id, id)
        assertEquals(saved.url, url)
    }

    @Test
    fun `get non-existent id throws not found`() {
        val id = "a1B2c3"
        every { repo.findById(any()) } returns Optional.empty()
        assertThrows(EntityNotFoundException::class.java) { service.get(id) }
        verify { repo.findById(id) }
    }

    @Test
    fun `create persists valid entity`() {
        val url = "https://example.com"
        every { repo.existsById(any()) } returns false
        every { repo.save(any<UrlEntry>()) } answers { firstArg() }
        val saved = service.create(url)
        verify { repo.save(any<UrlEntry>()) }
        assertEquals(url, saved.url)
        assertNotNull(saved.id)
        val base64 = Regex("^[A-Za-z0-9_-]*$")
        assert(base64.matches(saved.id))
    }

    @Test
    fun `create retries on collision`() {
        // Simulate first ID colliding, second unique
        val dupId = "dupId"
        val uniqId = "uniqId"
        val url = "https://retry.example"
        every { repo.existsById(dupId) } returns true
        every { repo.existsById(uniqId) } returns false
        every { repo.save(any<UrlEntry>()) } answers { firstArg() }

        mockkObject(IdGenerator)
        every { IdGenerator.generateId() } returnsMany listOf(dupId, uniqId)

        val saved = service.create(url)
        assertEquals(url, saved.url)
        assertEquals(uniqId, saved.id)
    }

    @Test
    fun `create fails after 5 duplicate key collisions`() {
        mockkObject(IdGenerator)
        every { IdGenerator.generateId() } returnsMany listOf("dup1", "dup2", "dup3", "dup4", "dup5")
        every { repo.existsById(any()) } returns true  // every generated key already exists
        assertThrows(IllegalStateException::class.java) {
            service.create("https://example.com")
        }
    }
}
