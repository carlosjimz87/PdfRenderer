package com.carlosjimz87.pdfrenderer.api

import com.carlosjimz87.pdfrenderer.Constants
import com.carlosjimz87.pdfrenderer.Constants.ACCESS_TOKEN
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiBuilder {
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
                    .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Connection", "keep-alive")
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun getPdfCall(
        url: String,
        getPdfDataIn: GetPdfDataIn,
        apiService: ApiService,
        callback: PdfDownloadCallback
    ) {
        val call = apiService.getFile(url, getPdfDataIn)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
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