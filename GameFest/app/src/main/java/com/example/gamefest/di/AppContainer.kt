package com.example.gamefest.di

import android.content.Context
import com.example.gamefest.data.local.GameFestDatabase
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.PersistentCookieJar
import com.example.gamefest.data.repository.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

interface AppContainer {
    val publisherRepository: PublisherRepository
    val gameRepository: GameRepository
    val userRepository: UserRepository
    fun clearCookies()
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://162.38.111.36/api/"

    private val persistentCookieJar = PersistentCookieJar(context)

    private val okHttpClient: OkHttpClient by lazy {
        val trustAllCerts = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustAllCerts), SecureRandom())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts)
            .hostnameVerifier { _, _ -> true }
            .cookieJar(persistentCookieJar)
            .addInterceptor(logging)
            .build()
    }

    override fun clearCookies() {
        persistentCookieJar.clearAll()
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    private val retrofitService: GameFestApiService by lazy {
        retrofit.create(GameFestApiService::class.java)
    }

    private val database: GameFestDatabase by lazy {
        GameFestDatabase.getDatabase(context)
    }

    override val publisherRepository: PublisherRepository by lazy {
        PublisherRepositoryImpl(database.publisherDao(), retrofitService)
    }

    override val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(database.gameDao(), retrofitService)
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userDao(), retrofitService, persistentCookieJar)
    }
}
