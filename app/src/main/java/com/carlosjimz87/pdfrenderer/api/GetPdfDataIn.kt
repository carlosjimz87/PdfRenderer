package com.carlosjimz87.pdfrenderer.api

import com.carlosjimz87.pdfrenderer.Constants.LANG_NAME
import com.carlosjimz87.pdfrenderer.Constants.NOTIF_ID_NAME
import com.google.gson.annotations.SerializedName

data class GetPdfDataIn(
    @SerializedName(LANG_NAME)
    val language: String,
    @SerializedName(NOTIF_ID_NAME)
    val id: String
)
