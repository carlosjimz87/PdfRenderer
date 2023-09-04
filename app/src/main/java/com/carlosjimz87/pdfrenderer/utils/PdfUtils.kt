package com.carlosjimz87.pdfrenderer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.widget.ImageView
import com.carlosjimz87.pdfrenderer.Constants
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

object PdfUtils {

    lateinit var pdfRenderer: PdfRenderer

    interface PdfRendererCallback {
        fun onRendered(totalPages: Int)
        fun onFailure()
    }

//    fun renderPdf(
//        webView: WebView,
//        responseBody: ResponseBody,
//        onRenderError: (String) -> Unit = {}
//    ) {
//        try {
//            val bytes = responseBody.bytes()
//            val base64PDF = Base64.encodeToString(bytes, Base64.DEFAULT)
//            webView.loadData(base64PDF, "text/html", "utf-8")
//        } catch (e: IOException) {
//            Log.e(TAG, "Error converting PDF to Base64", e)
//            onRenderError("Error loading PDF")
//        }
//    }

    fun renderPdf(
        webView: WebView,
        responseBody: ResponseBody,
        onRenderError: (String) -> Unit = {}
    ) {
        try {
            val inputStream = GZIPInputStream(responseBody.byteStream())
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            val base64PDF = stringBuilder.toString()
            webView.loadData(base64PDF, "text/html", "utf-8")

        } catch (e: IOException) {
            Log.e(TAG, "Error handling PDF", e)
            onRenderError("Error loading PDF")
        }
    }


    fun renderPdf(
        file: File,
        imageView: ImageView,
        callback: PdfRendererCallback,
        pageIndex: Int? = 0,
    ) {

        // Render the first page of the PDF as a Bitmap and show it in an ImageView
        try {
            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)
            val totalPages = pdfRenderer.pageCount
            if (totalPages > 0) {
                val page = pdfRenderer.openPage(pageIndex ?: 0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
                page.close()
            }
            pdfRenderer.close()
            fileDescriptor.close()
            callback.onRendered(totalPages)
        } catch (e: IOException) {
            e.printStackTrace()
            callback.onFailure()
        }
    }

//                        var inputStream: InputStream? = response.body()?.byteStream()
//                        if (contentEncoding == "gzip") {
//                            inputStream = GZIPInputStream(inputStream)
//                        }
//                        if (inputStream != null) {
//                            saveToCache(inputStream, context)
//                        }

    fun renderGzipPdf(
        webView: WebView, responseBody: ResponseBody,
        onRenderError: (String) -> Unit = {}
    ) {
        try {
            var inputStream: InputStream? = responseBody.byteStream()
            inputStream = GZIPInputStream(inputStream)

            // Create an output stream to hold the decompressed data
            val byteArrayOutputStream = ByteArrayOutputStream()

            // Buffer for reading from the GZIPInputStream
            val buffer = ByteArray(1024)
            var bytesRead: Int

            // Read the compressed data into the buffer and write it to the ByteArrayOutputStream
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }

            // Close the streams
            byteArrayOutputStream.close()
            inputStream.close()

            // Convert the decompressed data to Base64
            val base64PDF =
                Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

            // HTML string for loading the Base64 PDF into an iframe
            val html =
                "<iframe src='data:application/pdf;base64,$base64PDF' width='100%' height='100%'></iframe>"

            // Load the HTML string into the WebView
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        } catch (e: IOException) {
            Log.e("WebView", "Error loading PDF into WebView", e)
            onRenderError("Error loading PDF (${e.message})")
        }
    }


    fun renderFilePdf(
        context: Context, webView: WebView,
        onRenderError: (String) -> Unit = {}
    ) {
        try {
            val file = File(context.cacheDir, Constants.PDF_FILE_NAME)
            if (file.exists() && file.isFile && file.extension == Constants.PDF_FILE_EXT) {
                webView.loadUrl("file://${file.absolutePath}")
            }
        } catch (e: IOException) {
            onRenderError("Error loading PDF (${e.message})")
        }
    }
}