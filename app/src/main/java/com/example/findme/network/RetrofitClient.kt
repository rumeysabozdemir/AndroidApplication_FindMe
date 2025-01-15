package com.example.findme.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // API Base URLs
    private const val CELL_ID_BASE_URL = "https://us1.unwiredlabs.com/v2/"
    private const val IP_BASE_URL = "http://ip-api.com/"
    private const val PHONE_NUMBER_API_BASE_URL = "http://apilayer.net/api/"

    // PhoneNumber API için Retrofit Instance
    val phoneNumberApi: PhoneNumberApi by lazy {
        Retrofit.Builder()
            .baseUrl(PHONE_NUMBER_API_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhoneNumberApi::class.java)
    }

    // OpenCellID API için Retrofit örneği
    val cellIdInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CELL_ID_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // IP API için Retrofit örneği
    val ipInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(IP_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Key alma fonksiyonu (PhoneNumber API)
    fun getPhoneNumberApiKey(context: Context): String {
        return context.getString(com.example.findme.R.string.phone_number_api_key)
    }

    // OpenCellID API Key alma fonksiyonu
    fun getApiKey(context: Context): String {
        return context.getString(com.example.findme.R.string.opencellid_api_key)
    }

    // OkHttpClient ile Logging Interceptor ekleyin
    private fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}