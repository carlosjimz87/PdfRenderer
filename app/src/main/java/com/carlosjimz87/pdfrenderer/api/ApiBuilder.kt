package com.carlosjimz87.pdfrenderer.api

import android.content.Context
import com.carlosjimz87.pdfrenderer.utils.FileUtils
import com.carlosjimz87.pdfrenderer.utils.PdfUtils
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiBuilder {
    const val BASE_URL = "https://apipre.caixabankconsumer.com"
    const val PDF_URL= "/cpc/v1.0/crm365/notificaciones/obtener"
    const val ACCESS_TOKEN = "27eb1715-7122-4bf2-921a-c2545a57b43a-1693840013" //TODO: change this to the latest
    const val NOTIF_ID = "cfd02fdd-d733-eb11-9117-0050568f8b74"
    const val LANG = "es"
    const val LANG_KEY = "Idioma"
    const val NOTIF_ID_KEY = "IdNotificacion"
    const val AUTHORIZATION_KEY = "Authorization"
    const val CONTENT_TYPE_KEY = "Content-Type"
    const val CONTENT_ENCODING_KEY = "Content-Encoding"
    const val ACCEPT_KEY = "Accept"
    const val ACCEPT_ENCODING_KEY = "Accept-Encoding"
    const val CONNECTION_KEY = "Connection"

    const val APPLICATION_JSON_VALUE = "application/json"
    const val ENCODING_GZIP_VALUE = "gzip, deflate, br"
    const val KEEP_ALIVE_VALUE = "keep-alive"

    interface PdfDownloadCallback {
        fun onSuccess(responseBody: ResponseBody)
        fun onFailure(message: String)
    }

    fun build(): ApiService {
        // Initialize logging interceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .addHeader(AUTHORIZATION_KEY, "Bearer $ACCESS_TOKEN")
                    .addHeader(CONTENT_TYPE_KEY, APPLICATION_JSON_VALUE)
                    .addHeader(ACCEPT_KEY, "*/*")
                    .addHeader(ACCEPT_ENCODING_KEY, ENCODING_GZIP_VALUE)
                    .addHeader(CONNECTION_KEY, KEEP_ALIVE_VALUE)
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun getPdfCall(
        context: Context,
        url: String,
        dataIn: GetPdfDataIn,
        api: ApiService,
        saveToFile: Boolean? = false,
        callback: PdfDownloadCallback
    ) {
        val call = api.getFile(url, dataIn)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    if (PdfUtils.isResponsePdf(response)) {
                        if (saveToFile == true) {
                            val byteStream = response.body()?.byteStream()
                            FileUtils.saveGzipToDisk(context, byteStream)
                        } else {
                            callback.onSuccess(response.body()!!)
                        }
                        response.body()?.let { callback.onSuccess(it) }
                    }
                } else {
                    callback.onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.onFailure(t.message ?: "Unknown error")
            }
        })
    }
}