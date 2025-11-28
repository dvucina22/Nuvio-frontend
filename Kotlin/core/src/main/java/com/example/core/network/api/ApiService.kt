package com.example.core.network.api

import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.LoginResponse
import com.example.core.auth.dto.OAuthVerifyRequest
import com.example.core.auth.dto.OAuthVerifyResponse
import com.example.core.auth.dto.RegisterRequest
import com.example.core.auth.dto.RegisterResponse
import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.AddCardResponse
import com.example.core.cards.dto.CardsResponse
import com.example.core.cards.dto.DeleteCardResponse
import com.example.core.cards.dto.PrimaryCardResponse
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.FavoriteRequest
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductFilterRequest
import com.example.core.catalog.dto.ProductResponse
import com.example.core.user.dto.ChangePasswordRequest
import com.example.core.user.dto.ChangePasswordResponse
import com.example.core.user.dto.UpdateProfilePictureRequest
import com.example.core.user.dto.UpdateProfilePictureResponse
import com.example.core.user.dto.UpdateUserRequest
import com.example.core.user.dto.UpdateUserResponse
import com.example.core.user.dto.UploadSignatureResponse
import com.example.core.user.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("accounts/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("accounts/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("accounts/logged-user")
    suspend fun getUser(): Response<UserDto>
    @PUT("accounts/logged-user")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<UpdateUserResponse>
    @POST("accounts/update-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>
    @POST("accounts/oauth/{provider}/verify")
    suspend fun verifyOAuth(@Path("provider") provider: String, @Body request: OAuthVerifyRequest): Response<OAuthVerifyResponse>

    @GET("accounts/profile-picture/upload-signature")
    suspend fun getUploadSignature(): Response<UploadSignatureResponse>

    @PUT("accounts/profile-picture/update")
    suspend fun updateProfilePicture(@Body request: UpdateProfilePictureRequest): Response<UpdateProfilePictureResponse>

    @GET("transactions/cards")
    suspend fun getCards(): CardsResponse
    @POST("transactions/cards")
    suspend fun addCard(@Body request: AddCardRequest): Response<AddCardResponse>
    @DELETE("transactions/cards/{card_id}")
    suspend fun deleteCard(
        @Path("card_id") cardId: Int
    ): Response<DeleteCardResponse>
    @PUT("transactions/cards/{card_id}/primary")
    suspend fun setPrimaryCard(@Path("card_id") cardId: Int): Response<PrimaryCardResponse>

    @POST("catalog/products/filter")
    suspend fun filterProducts(@Body request: ProductFilterRequest): Response<List<Product>>

    @GET("catalog/brands")
    suspend fun getAllBrands(): Response<List<Brand>>

    @GET("catalog/categories")
    suspend fun getAllCategories(): Response<List<Category>>

    @POST("catalog/products/favorite")
    suspend fun addFavoriteProduct(@Body request: FavoriteRequest): Response<Unit>

    @HTTP(method = "DELETE", path = "catalog/products/favorite", hasBody = true)
    suspend fun removeFavoriteProduct(@Body request: FavoriteRequest): Response<Unit>

    @GET("catalog/attributes")
    suspend fun getAttributes(): Response<List<AttributeFilter>>
}