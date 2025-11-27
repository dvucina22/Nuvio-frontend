package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttributeFilter(
    val attributeId: Long,
    val values: List<String>
)
