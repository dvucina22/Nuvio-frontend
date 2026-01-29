package com.example.core.network.api

import com.example.core.auth.dto.LoginRequest
import com.example.core.auth.dto.LoginResponse
import com.example.core.auth.dto.OAuthVerifyRequest
import com.example.core.auth.dto.OAuthVerifyResponse
import com.example.core.auth.dto.RegisterRequest
import com.example.core.auth.dto.RegisterResponse
import com.example.core.auth.dto.Role
import com.example.core.cards.dto.AddCardRequest
import com.example.core.cards.dto.AddCardResponse
import com.example.core.cards.dto.CardsResponse
import com.example.core.cards.dto.DeleteCardResponse
import com.example.core.cards.dto.PrimaryCardResponse
import com.example.core.cart.dto.CartItemDto
import com.example.core.catalog.dto.AddProductRequest
import com.example.core.catalog.dto.AddProductResponse
import com.example.core.catalog.dto.AttributeFilter
import com.example.core.catalog.dto.Brand
import com.example.core.catalog.dto.Category
import com.example.core.catalog.dto.FavoriteRequest
import com.example.core.catalog.dto.Product
import com.example.core.catalog.dto.ProductDetail
import com.example.core.catalog.dto.ProductFilterRequest
import com.example.core.catalog.dto.SuccessfulDeleteResponse
import com.example.core.catalog.dto.UpdateProductRequest
import com.example.core.catalog.dto.UpdateProductResponse
import com.example.core.network.dto.ApiResponse
import com.example.core.network.interceptor.AuthInterceptor
import com.example.core.sale.dto.SaleRequest
import com.example.core.sale.dto.SaleResponse
import com.example.core.statistics.dto.TransactionStatisticsResponse
import com.example.core.transactions.dto.TransactionDetail
import com.example.core.transactions.dto.TransactionFilterRequest
import com.example.core.transactions.dto.TransactionListResponse
import com.example.core.user.dto.ChangePasswordRequest
import com.example.core.user.dto.ChangePasswordResponse
import com.example.core.user.dto.UpdateProfilePictureRequest
import com.example.core.user.dto.UpdateProfilePictureResponse
import com.example.core.user.dto.UpdateUserRequest
import com.example.core.user.dto.UpdateUserResponse
import com.example.core.user.dto.UploadSignatureResponse
import com.example.core.user.dto.UserDto
import com.example.core.user.dto.UserListItemDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("catalog/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductDetail>
    @POST("catalog/products")
    suspend fun addNewProduct(@Body request: AddProductRequest): Response<AddProductResponse>
    @DELETE("catalog/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<SuccessfulDeleteResponse>

    @PUT("catalog/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body request: UpdateProductRequest
    ): Response<UpdateProductResponse>
    @GET("catalog/products/cart")
    suspend fun getCartItems(): Response<List<CartItemDto>>
    @POST("catalog/products/cart/{id}")
    suspend fun addCartItem(@Path("id") productId: Int): Response<Unit>
    @DELETE("catalog/products/cart/{id}")
    suspend fun deleteCartItem(@Path("id") productId: Int): Response<Unit>

    @GET("catalog/products/cart/empty")
    suspend fun clearCart(): Response<Unit>

    @POST("transactions/sale")
    suspend fun makeSale(@Body request: SaleRequest): Response<SaleResponse>
    @GET("accounts/users")
    suspend fun getUsers(): Response<List<UserListItemDto>>

    @GET("accounts/users/filter")
    suspend fun filterUsers(@Query("name") name: String): Response<List<UserListItemDto>>

    @DELETE("accounts/users/{id}") suspend fun deactivateUser(@Path("id") userId: String): Response<Unit>

    @GET("accounts/roles")
    suspend fun getAllRoles(): Response<List<Role>>

    @POST("accounts/roles/{role_id}/user/{user_id}")
    suspend fun addUserRole(@Path("role_id") roleId: Int, @Path("user_id") userId: String): Response<Unit>

    @DELETE("accounts/roles/{role_id}/user/{user_id}")
    suspend fun removeUserRole(@Path("role_id") roleId: Int, @Path("user_id") userId: String): Response<Unit>

    @POST("transactions/history")
    suspend fun getTransactionHistory(@Body request: TransactionFilterRequest): Response<com.example.core.network.dto.ApiResponse<com.example.core.transactions.dto.TransactionListResponse>>

    @GET("transactions/history/{id}")
    suspend fun getTransactionDetail(@Path("id") id: Long): ApiResponse<TransactionDetail>

    @GET("transactions/statistics")
    suspend fun getTransactionStatistics(): Response<TransactionStatisticsResponse>

    @POST("transactions/sale/{transaction_id}/void")
    suspend fun voidTransaction(
        @Path("transaction_id") transactionId: Long
    ): Response<Unit>
}