package com.carlosjimz87.pdfrenderer.api

import com.carlosjimz87.pdfrenderer.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

object ApiBuilder {
    interface PdfDownloadCallback {
        fun onSuccess(responseBody: ResponseBody)
        fun onFailure(message: String)
    }

    fun build(): ApiService {

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun getPdfCall(url: String, apiService: ApiService, callback: PdfDownloadCallback) {
        val call = apiService.downloadFile(url)

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