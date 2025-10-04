package com.example.urlservice.domain

import org.springframework.data.jpa.repository.JpaRepository

interface UrlRepository : JpaRepository<UrlEntry, String>
