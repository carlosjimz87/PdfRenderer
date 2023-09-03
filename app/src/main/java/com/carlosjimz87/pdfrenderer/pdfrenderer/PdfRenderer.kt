package com.carlosjimz87.pdfrenderer.pdfrenderer

import android.util.Base64
import android.util.Log
import android.webkit.WebView
import com.carlosjimz87.pdfrenderer.utils.TAG
import okhttp3.ResponseBody
import java.io.IOException

object PdfRenderer {
    fun render(webView: WebView,responseBody: ResponseBody, onRenderError: (String) -> Unit = {}) {
        try {
            val bytes = responseBody.bytes()
            val base64PDF = Base64.encodeToString(bytes, Base64.DEFAULT)
            webView.loadData(base64PDF, "text/html", "utf-8")
        } catch (e: IOException) {
            Log.e(TAG, "Error converting PDF to Base64", e)
            onRenderError("Error loading PDF")
        }
    }
}