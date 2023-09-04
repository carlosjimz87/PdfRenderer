package com.carlosjimz87.pdfrenderer.api

import android.content.Context
import android.util.Base64
import com.carlosjimz87.pdfrenderer.utils.FileUtils
import com.carlosjimz87.pdfrenderer.utils.FileUtils.PDF_ENCODED_FILE_NAME
import com.carlosjimz87.pdfrenderer.utils.PdfUtils
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.zip.GZIPInputStream

object ApiBuilder {
    const val BASE_URL = "https://apipre.caixabankconsumer.com"
    const val PDF_URL = "/cpc/v1.0/crm365/notificaciones/obtener"
    const val ACCESS_TOKEN =
        "b6697e6e-b311-4275-9400-6311387d635e-1693848461" //TODO: change this to the latest
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
        fun onSavedFile(filePath: String)
        fun onFailure(message: String)
    }

    fun build(intercept: Boolean? = false): ApiService {
        // Initialize logging interceptor
        val logging = createLogInterceptor(intercept)

        val client = createClient(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private fun createLogInterceptor(intercept: Boolean?): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (intercept == true) BODY else NONE
        }
    }

    private fun createClient(logging: HttpLoggingInterceptor): OkHttpClient {
        val client = Builder()
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
        return client
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
                        // cacheFile(context, saveToFile, response, true, callback)
                        cacheFile(context, saveToFile, response, false, callback)
                        callback.onSuccess(response.body()!!)
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

    private fun cacheFile(
        context: Context,
        saveToFile: Boolean?,
        response: Response<ResponseBody>,
        decoding: Boolean? = true,
        callback: PdfDownloadCallback
    ) {
        if (saveToFile != true) return
        Thread {

            val file = if (decoding == true) {
                val byteStream = response.body()?.byteStream()
                FileUtils.saveGzipToDisk(context, byteStream)
            } else {
                val base64 = FileUtils.gzipToBase64(GZIPInputStream(response.body()!!.byteStream()))
                FileUtils.saveToDisk(context, base64!!, name = PDF_ENCODED_FILE_NAME)
            }
            callback.onSavedFile(file.absolutePath)
        }.start()
    }
}