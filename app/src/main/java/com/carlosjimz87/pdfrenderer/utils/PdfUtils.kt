package com.carlosjimz87.pdfrenderer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.widget.ImageView
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object PdfUtils {

    lateinit var pdfRenderer: PdfRenderer

    interface PdfRendererCallback {
        fun onRendered(totalPages: Int)
        fun onFailure()
    }

    fun renderPdf(webView: WebView, responseBody: ResponseBody, onRenderError: (String) -> Unit = {}) {
        try {
            val bytes = responseBody.bytes()
            val base64PDF = Base64.encodeToString(bytes, Base64.DEFAULT)
            webView.loadData(base64PDF, "text/html", "utf-8")
        } catch (e: IOException) {
            Log.e(TAG, "Error converting PDF to Base64", e)
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


    fun saveToCache(responseBody: ResponseBody, context: Context, fileName: String): File? {
        // Define the cache directory
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)
            var fileSizeDownloaded: Long = 0
            inputStream = responseBody.byteStream()
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()
            return file

        } catch (e: IOException) {
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}