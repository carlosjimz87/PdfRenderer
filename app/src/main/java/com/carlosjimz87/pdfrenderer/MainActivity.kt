package com.carlosjimz87.pdfrenderer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carlosjimz87.pdfrenderer.Constants.LANG
import com.carlosjimz87.pdfrenderer.Constants.NOTIF_ID
import com.carlosjimz87.pdfrenderer.api.ApiBuilder
import com.carlosjimz87.pdfrenderer.api.GetPdfDataIn
import com.carlosjimz87.pdfrenderer.databinding.ActivityMainBinding
import com.carlosjimz87.pdfrenderer.utils.FileUtils
import com.carlosjimz87.pdfrenderer.utils.PdfUtils
import com.carlosjimz87.pdfrenderer.utils.TAG
import com.carlosjimz87.pdfrenderer.utils.WebUtils
import okhttp3.ResponseBody
import java.io.File


class MainActivity : AppCompatActivity(), WebUtils.ListenerWebView {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val apiService by lazy { ApiBuilder.build() }
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (binding.webView.progress < 100) {
                binding.webView.stopLoading()
                Log.d(TAG, "Timeout error")
            }
        }

        binding.webView.let { WebUtils.initializeWebView(it, this) }

        binding.btnReload.setOnClickListener {
            loadPdf()
        }
    }

    private fun loadPdf() {
        ApiBuilder.getPdfCall(this,
            url = Constants.PDF_URL,
            dataIn = GetPdfDataIn(LANG, NOTIF_ID),
            api = apiService,
            saveToFile = false,
            object : ApiBuilder.PdfDownloadCallback {
            override fun onSuccess(responseBody: ResponseBody) {

                PdfUtils.renderPdf(binding.webView,responseBody, onRenderError = {
                    onReceivedError(it)
                })
            }

            override fun onFailure(message: String) {
                Log.e(TAG, "API Failure: $message")
                onReceivedError("Failed to load PDF")
            }
        })

        handler.postDelayed(runnable, 15000) // add timeout functionality
    }

    override fun onPageStarted(url: String) {

        handler.postDelayed(runnable, 15000)
    }

    override fun onPageFinished(url: String) {
        handler.removeCallbacks(runnable)
    }

    override fun doUpdateVisitedHistory(url: String) {
    }

    override fun onReceivedError(error: String) {
        Toast.makeText(this, "error $error", Toast.LENGTH_SHORT).show()
    }

}