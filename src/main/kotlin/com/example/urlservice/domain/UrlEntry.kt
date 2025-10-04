package com.example.urlservice.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "urls")
class UrlEntry(
    @Id
    var id: String,

    @Column(nullable = false, length = 4096)
    var url: String
)
