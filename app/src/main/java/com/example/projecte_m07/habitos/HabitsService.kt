package com.example.projecte_m07.habitos

import com.example.recyclerview.habitos.Habito
import com.example.recyclerview.habitos.HabitoCreate
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface HabitsService {
    @GET("habitos")
    suspend fun getHabitos(): List<Habito>

    @POST("habitos")
    suspend fun createHabito(@Body habito: HabitoCreate): Habito

    @PUT("habitos/{id}")
    suspend fun updateHabito(@Path("id") id: Int, @Body habito: HabitoCreate): Habito

    @DELETE("habitos/{id}")
    suspend fun deleteHabito(@Path("id") id: Int): Response<Unit>
}

class HabitosAPI{
    companion object  {
        private var mAPI : HabitsService? = null
        private const val BASE_URL = "http://35.170.234.59:8000/"

        @Synchronized
        fun API(): HabitsService {
            if (mAPI == null){
                val client: OkHttpClient = getUnsafeOkHttpClient()
                val gsonHourMinute = GsonBuilder()
                    .setDateFormat("HH:mm")
                    .create()
                mAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsonHourMinute))
                    .baseUrl(BASE_URL)
                    .client(getUnsafeOkHttpClient())
                    .build()
                    .create(HabitsService::class.java)
            }
            return mAPI!!
        }

        private fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }

                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}