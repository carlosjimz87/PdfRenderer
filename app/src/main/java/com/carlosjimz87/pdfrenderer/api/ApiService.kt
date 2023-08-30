package com.carlosjimz87.pdfrenderer.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>
}