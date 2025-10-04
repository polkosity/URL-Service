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
        every { repo.findById(any()) } returns Optional.of(UrlEntry(id = "a1B2c3", url = "www.originenergy.com.au/electricity-gas/plans.html"))
        service.get("a1B2c3")
        verify { repo.findById("a1B2c3") }
        val saved = service.get("a1B2c3")
        assertNotNull(saved)
        assertEquals(saved.id, "a1B2c3")
        assertEquals(saved.url, "www.originenergy.com.au/electricity-gas/plans.html")
    }

    @Test
    fun `get non-existent id throws not found`() {
        every { repo.findById(any()) } returns Optional.empty()
        assertThrows(EntityNotFoundException::class.java) { service.get("a1B2c3") }
    }

    @Test
    fun `create persists valid entity`() {
        every { repo.existsById(any()) } returns false
        every { repo.save(any<UrlEntry>()) } answers { firstArg() }
        val saved = service.create("https://example.com")
        verify { repo.save(any<UrlEntry>()) }
        assertEquals("https://example.com", saved.url)
        assertNotNull(saved.id)
        val base64 = Regex("^[A-Za-z0-9_-]*$")
        assert(base64.matches(saved.id))
    }

    @Test
    fun `create retries on collision`() {
        // Simulate first ID colliding, second unique
        every { repo.existsById("dupId") } returns true
        every { repo.existsById("uniqId") } returns false
        every { repo.save(any<UrlEntry>()) } answers { firstArg() }

        mockkObject(IdGenerator)
        every { IdGenerator.generateId() } returnsMany listOf("dupId", "uniqId")

        val saved = service.create("https://retry.example")
        assertEquals("https://retry.example", saved.url)
        assertEquals("uniqId", saved.id)
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
