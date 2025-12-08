package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttributeItem(
    val id: Long,
    val value: String
)
