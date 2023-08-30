package com.carlosjimz87.pdfrenderer

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carlosjimz87.pdfrenderer.api.ApiBuilder
import com.carlosjimz87.pdfrenderer.databinding.ActivityMainBinding
import com.carlosjimz87.pdfrenderer.listeners.GestureListener
import com.carlosjimz87.pdfrenderer.utils.FileUtils
import com.carlosjimz87.pdfrenderer.utils.PdfUtils
import com.carlosjimz87.pdfrenderer.utils.TAG
import java.io.File


class MainActivity : AppCompatActivity(){

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var gestureDetector : GestureDetector
    private var currentPage = 0
    private var totalPageCount = 0
    private var downloadedFilePath: String? = null
    private val PDF_URL = Constants.BASE_URL + Constants.PDF_URL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        gestureDetector = GestureDetector(this, GestureListener().apply {
            onSwipeRight = {
                swipeRight()
            }
            onSwipeLeft = {
                swipeLeft()
            }
        })

        binding.btnReload.setOnClickListener {
            loadPdf()
        }
        loadPdf()
    }

    private fun loadPdf(page:Int?=0) {
        binding.progressBar.visibility = View.VISIBLE

        if(downloadedFilePath != null){
            renderPdfFromFile(File(downloadedFilePath!!), page)
            return
        }

        ApiBuilder.getPdfCall(PDF_URL, ApiBuilder.build(), object : ApiBuilder.PdfDownloadCallback {

            override fun onSuccess(responseBody: okhttp3.ResponseBody) {
                Log.d(TAG, "l> onSuccess: ${responseBody.contentLength()}")

                val file = FileUtils.saveToDisk(this@MainActivity, responseBody)
                downloadedFilePath = file.absolutePath

                renderPdfFromFile(file, page)
            }

            override fun onFailure(message: String) {
                Log.d(TAG, "l> onFailure: $message")
            }
        })

    }

    private fun renderPdfFromFile(file: File, page: Int?) {
        PdfUtils.renderPdf(file, binding.imageView, object : PdfUtils.PdfRendererCallback {
            override fun onRendered(totalPages: Int) {
                Log.d(TAG, "l> onRendered: $page - $totalPages")
                binding.progressBar.visibility = View.GONE
                totalPageCount = totalPages
            }

            override fun onFailure() {
                binding.progressBar.visibility = View.GONE
            }
        }, page)
    }
    private fun swipeRight() {
        if (currentPage > 0) {
            currentPage--
            loadPdf(currentPage)
        }
    }

    private fun swipeLeft() {
        if (currentPage < totalPageCount - 1) {
            currentPage++
            loadPdf(currentPage)
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }
}