package com.smartguy.recipesapp.di

import com.example.recipes.BuildConfig
import com.smartguy.recipesapp.data.network.ApiKeyInterceptor
import com.smartguy.recipesapp.data.network.SpoonacularApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.spoonacular.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton @Named("apiKey")
    fun provideApiKey(): String = BuildConfig.SPOONACULAR_API_KEY

    @Provides @Singleton
    fun provideOkHttp(
        @Named("apiKey") apiKey: String
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .addInterceptor(logger)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): SpoonacularApi =
        retrofit.create(SpoonacularApi::class.java)
}