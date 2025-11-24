package com.example.nuviofrontend.feature.profile.data

import android.content.Context
import android.net.Uri
import com.example.core.user.dto.UploadSignatureResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CloudinaryService @Inject constructor(private val context: Context) {
    private val client = OkHttpClient()

    suspend fun uploadImage(
        imageUri: Uri,
        uploadSignature: UploadSignatureResponse
    ): String = withContext(Dispatchers.IO) {
        val file = uriToFile(imageUri)

        try {
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("api_key", uploadSignature.apiKey)
                .addFormDataPart("timestamp", uploadSignature.timestamp.toString())
                .addFormDataPart("signature", uploadSignature.signature)
                .addFormDataPart("upload_preset", uploadSignature.uploadPreset)
                .addFormDataPart("folder", uploadSignature.folder)

            uploadSignature.publicId?.let { publicId ->
                requestBodyBuilder.addFormDataPart("public_id", publicId)
            }

            val requestBody = requestBodyBuilder.build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/${uploadSignature.cloudName}/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Unknown error"
                throw Exception("Upload failed (${response.code}): $errorBody")
            }

            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            val json = JSONObject(responseBody)

            json.getString("secure_url")
        } finally {
            file.delete()
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")

        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        return file
    }
}