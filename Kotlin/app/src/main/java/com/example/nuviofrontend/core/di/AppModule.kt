package com.example.nuviofrontend.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.cards.ICardRepository
import com.example.core.cart.ICartRepository
import com.example.core.catalog.IProductRepository
import com.example.core.network.api.ApiClient
import com.example.core.network.api.ApiService
import com.example.core.network.interceptor.AuthInterceptor
import com.example.core.network.token.IUserPrefs
import com.example.core.sale.ISaleService
import com.example.core.transactions.ITransactionRepository
import com.example.nuviofrontend.feature.cart.data.CartRepository
import com.example.nuviofrontend.feature.cart.data.CartService
import com.example.nuviofrontend.feature.catalog.data.CatalogRepository
import com.example.nuviofrontend.feature.catalog.data.CatalogService
import com.example.nuviofrontend.feature.catalog.data.ProductImageRepository
import com.example.nuviofrontend.feature.catalog.data.ProductRepository
import com.example.nuviofrontend.feature.catalog.data.ProductService
import com.example.nuviofrontend.feature.profile.data.CardRepository
import com.example.nuviofrontend.feature.profile.data.CardService
import com.example.nuviofrontend.feature.profile.data.CloudinaryService
import com.example.nuviofrontend.feature.profile.data.ProfilePictureRepository
import com.example.nuviofrontend.feature.profile.data.UserRepository
import com.example.nuviofrontend.feature.profile.data.UserService
import com.example.nuviofrontend.feature.sale.data.SaleRepository
import com.example.nuviofrontend.feature.sale.data.SaleService
import com.example.nuviofrontend.feature.statistics.data.StatisticsRepository
import com.example.nuviofrontend.feature.statistics.data.StatisticsService
import com.example.nuviofrontend.feature.transactions.data.TransactionRepository
import com.example.nuviofrontend.feature.transactions.data.TransactionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit =
        ApiClient.create(authInterceptor)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideUserService(apiService: ApiService): UserService {
        return UserService(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService,
        userPrefs: IUserPrefs
    ): UserRepository {
        return UserRepository(userService, userPrefs)
    }

    @Provides
    @Singleton
    fun provideCloudinaryService(@ApplicationContext context: Context): CloudinaryService {
        return CloudinaryService(context)
    }

    @Provides
    @Singleton
    fun provideProfilePictureRepository(
        apiService: ApiService,
        cloudinaryService: CloudinaryService
    ): ProfilePictureRepository {
        return ProfilePictureRepository(apiService, cloudinaryService)
    }

    @Provides
    @Singleton
    fun provideCardService(apiService: ApiService): CardService {
        return CardService(apiService)
    }

    @Provides
    @Singleton
    fun provideCardRepository(
        cardService: CardService,
        userPrefs: IUserPrefs
    ): ICardRepository {
        return CardRepository(cardService, userPrefs)
    }

    @Provides
    @Singleton
    fun provideCatalogService(apiService: ApiService): CatalogService {
        return CatalogService(apiService)
    }

    @Provides
    @Singleton
    fun provideCatalogRepository(catalogService: CatalogService): CatalogRepository {
        return CatalogRepository(catalogService)
    }

    @Provides
    @Singleton
    fun provideCartService(apiService: ApiService): CartService {
        return CartService(apiService)
    }

    @Provides
    @Singleton
    fun provideCartRepository(cartService: CartService) : ICartRepository {
        return CartRepository(cartService)
    }

    @Provides
    @Singleton
    fun provideProductService(apiService: ApiService): ProductService {
        return ProductService(apiService)
    }

    @Provides
    @Singleton
    fun provideProductRepository(productService: ProductService) : IProductRepository {
        return ProductRepository(productService)
    }

    @Provides
    @Singleton
    fun provideProductImageRepository(apiService: ApiService, cloudinaryService: CloudinaryService) : ProductImageRepository{
        return ProductImageRepository(apiService, cloudinaryService)
    }

    @Provides
    @Singleton
    fun provideSaleService(apiService: ApiService): ISaleService {
        return SaleService(apiService)
    }

    @Provides
    @Singleton
    fun provideSaleRepository(saleService: ISaleService): SaleRepository {
        return SaleRepository(saleService)
    }

    @Provides
    @Singleton
    fun provideTransactionService(apiService: ApiService): TransactionService {
        return TransactionService(apiService)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionService: TransactionService): TransactionRepository {
        return TransactionRepository(transactionService)
    }

    @Provides
    @Singleton
    fun provideTransactionRepositoryInterface(transactionService: TransactionService): ITransactionRepository {
        return TransactionRepository(transactionService)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideStatisticsService(apiService: ApiService): StatisticsService{
        return StatisticsService(apiService)
    }

    @Provides
    @Singleton
    fun provideStatisticsRepository(statisticsService: StatisticsService): StatisticsRepository{
        return StatisticsRepository(statisticsService)
    }
}
