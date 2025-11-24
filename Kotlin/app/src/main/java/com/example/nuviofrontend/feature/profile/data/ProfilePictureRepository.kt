package com.example.nuviofrontend.feature.profile.data

import android.net.Uri
import com.example.core.network.api.ApiService
import com.example.core.user.dto.UpdateProfilePictureRequest
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProfilePictureRepository @Inject constructor(
    private val apiService: ApiService,
    private val cloudinaryService: CloudinaryService
) {

    suspend fun uploadProfilePicture(imageUri: Uri): String {
        val signatureResponse = apiService.getUploadSignature()
        if (!signatureResponse.isSuccessful) {
            throw HttpException(signatureResponse)
        }
        val signature = signatureResponse.body() ?: throw IOException("Empty signature response")

        val imageUrl = cloudinaryService.uploadImage(imageUri, signature)

        val updateResponse = apiService.updateProfilePicture(
            UpdateProfilePictureRequest(imageUrl)
        )
        if (!updateResponse.isSuccessful) {
            throw HttpException(updateResponse)
        }

        return imageUrl
    }
}