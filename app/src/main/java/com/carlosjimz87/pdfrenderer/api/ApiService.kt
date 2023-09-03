package com.carlosjimz87.pdfrenderer.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST
    fun getFile(@Url fileUrl: String, @Body request: GetPdfDataIn): Call<ResponseBody>
}