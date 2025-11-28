package com.example.core.catalog.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttributeFilter(
    val attributeId: Long? = null,
    val name: String? = null,
    val values: List<String>
)
