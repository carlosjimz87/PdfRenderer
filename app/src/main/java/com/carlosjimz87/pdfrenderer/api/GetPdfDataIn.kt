package com.carlosjimz87.pdfrenderer.api

import com.carlosjimz87.pdfrenderer.api.ApiBuilder.LANG_KEY
import com.carlosjimz87.pdfrenderer.api.ApiBuilder.NOTIF_ID_KEY
import com.google.gson.annotations.SerializedName

data class GetPdfDataIn(
    @SerializedName(LANG_KEY)
    val language: String,
    @SerializedName(NOTIF_ID_KEY)
    val id: String
)
