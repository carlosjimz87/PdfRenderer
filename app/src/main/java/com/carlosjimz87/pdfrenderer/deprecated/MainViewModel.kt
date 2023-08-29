//package com.carlosjimz87.pdfrenderer
//
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.os.ParcelFileDescriptor
//import android.widget.ImageView
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.carlosjimz87.pdfrenderer.Constants.PDF_TEMP_EXT
//import com.carlosjimz87.pdfrenderer.Constants.PDF_TEMP_FILE
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.io.File
//import java.io.IOException
//
//class MainViewModel: ViewModel() {
//    private val _pdfBytes = MutableLiveData<ByteArray?>()
//    val pdfBytes: LiveData<ByteArray?> = _pdfBytes
//
//    private val tempFile = File.createTempFile(PDF_TEMP_FILE, PDF_TEMP_EXT)
//    private val pdfRenderer:PdfRenderer by lazy {
//        PdfRenderer(ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY))
//    }
//
//    fun renderPdf(imageView: ImageView){
//        // Open the PDF file
//
//        // Get the first page
//        val pageCount = pdfRenderer.pageCount
//        if (pageCount > 0) {
//            val page = pdfRenderer.openPage(0)
//
//            // Create a bitmap to render the page into
//            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//
//            // Render the page into the bitmap
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//
//            // Set the bitmap into the ImageView
//            imageView.setImageBitmap(bitmap)
//
//            // Close the page when done
//            page.close()
//        }
//
//        // Close the renderer when done
//        pdfRenderer.close()
//    }
//
//    fun renderPdfWithBytes(imageView: ImageView, bytes: ByteArray) {
//        // Write the bytes to a temporary file
//        tempFile.writeBytes(bytes)
//
//        // Get the first page
//        val pageCount = pdfRenderer.pageCount
//        if (pageCount > 0) {
//            val page = pdfRenderer.openPage(0)
//
//            // Create a bitmap to render the page into
//            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//
//            // Render the page into the bitmap
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//
//            // Set the bitmap into the ImageView
//            imageView.setImageBitmap(bitmap)
//
//            // Close the page when done
//            page.close()
//        }
//
//
//    }
//
//    fun closeRenderer() {
//        // Close the renderer when done
//        pdfRenderer.close()
//
//        // Delete the temporary file
//        tempFile.delete()
//    }
//
//
//    fun downloadPdf(url: String) {
//        viewModelScope.launch {
//            val bytes = withContext(Dispatchers.IO) {
//                val client = OkHttpClient()
//                val request = Request.Builder()
//                    .url(url)
//                    .build()
//
//                try {
//                    client.newCall(request).execute().use { response ->
//                        if (!response.isSuccessful) {
//                            throw IOException("Failed to download PDF: ${response.message}")
//                        }
//                        response.body?.bytes()
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    null
//                }
//            }
//
//            _pdfBytes.value = bytes
//        }
//    }
//}