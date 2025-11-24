package com.example.core.user.dto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadSignatureResponse(
    val signature: String,
    val timestamp: Long,
    val cloudName: String,
    val apiKey: String,
    val uploadPreset: String,
    val folder: String,
    val publicId: String? = null
)