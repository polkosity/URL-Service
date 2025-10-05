package com.example.urlservice.web

import com.example.urlservice.domain.UrlEntry
import com.example.urlservice.domain.UrlRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerIntegrationTest(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val objectMapper: ObjectMapper,
    @param:Autowired private val repo: UrlRepository
) {
    @Test
    fun `create returns 201 and ID`() {
        val url = "https://example.com/path?q=1"
        val body = mapOf("url" to url)
        mockMvc.perform(
            post("/short.ly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.url").value(url))
    }

    @Test
    fun `create with invalid url returns 400`() {
        val body = mapOf("url" to "notaurl")
        mockMvc.perform(
            post("/short.ly")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `lookup existing returns 200 and info`() {
        val url = "https://www.originenergy.com.au/electricity-gas/plans.html"
        val saved = repo.save(UrlEntry(id = "a1B2c3", url = url))
        mockMvc.perform(get("/short.ly/${saved.id}/info"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(saved.id))
            .andExpect(jsonPath("$.url").value(url))
    }

    @Test
    fun `fetch existing entry returns 301`() {
        val url = "https://www.originenergy.com.au/electricity-gas/plans.html"
        val saved = repo.save(UrlEntry(id = "a1B2c3", url = url))
        mockMvc.perform(get("/short.ly/${saved.id}"))
            .andExpect(status().isMovedPermanently)
            .andExpect(header().string("Location", url))

    }

    @Test
    fun `lookup missing returns 404`() {
        mockMvc.perform(get("/short.ly/doesNotExist"))
            .andExpect(status().isNotFound)
    }
}
