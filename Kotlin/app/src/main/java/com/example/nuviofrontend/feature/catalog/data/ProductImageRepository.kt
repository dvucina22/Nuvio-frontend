package com.example.nuviofrontend.feature.catalog.data

import android.net.Uri
import com.example.core.network.api.ApiService
import com.example.core.user.dto.UpdateProfilePictureRequest
import com.example.nuviofrontend.feature.profile.data.CloudinaryService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductImageRepository @Inject constructor(
    private val apiService: ApiService,
    private val cloudinaryService: CloudinaryService
) {
    suspend fun uploadProductPicture(imageUri: Uri): String {
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