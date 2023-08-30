package com.carlosjimz87.pdfrenderer.utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.File
import java.io.IOException

object PdfUtils {

    lateinit var pdfRenderer: PdfRenderer

    interface PdfRendererCallback {
        fun onRendered()
        fun onFailure()
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
            val pageCount = pdfRenderer.pageCount
            if (pageCount > 0) {
                val page = pdfRenderer.openPage(pageIndex ?: 0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
                page.close()
            }
            pdfRenderer.close()
            fileDescriptor.close()
            callback.onRendered()
        } catch (e: IOException) {
            e.printStackTrace()
            callback.onFailure()
        }
    }
}