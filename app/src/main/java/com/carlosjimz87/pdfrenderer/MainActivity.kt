package com.carlosjimz87.pdfrenderer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.carlosjimz87.pdfrenderer.api.ApiBuilder
import com.carlosjimz87.pdfrenderer.databinding.ActivityMainBinding
import com.carlosjimz87.pdfrenderer.utils.FileUtils
import com.carlosjimz87.pdfrenderer.utils.PdfUtils
import com.carlosjimz87.pdfrenderer.utils.TAG


class MainActivity : AppCompatActivity() {
    private val PDF_URL= "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf"

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnReload.setOnClickListener {
            loadPdf()
        }
        loadPdf()
    }

    private fun loadPdf() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        ApiBuilder.getPdfCall(PDF_URL, ApiBuilder.build(), object : ApiBuilder.PdfDownloadCallback {
            override fun onSuccess(responseBody: okhttp3.ResponseBody) {
                Log.d(TAG, "l> onSuccess: ${responseBody.contentLength()}")

                val file = FileUtils.saveToDisk(this@MainActivity, responseBody)

                PdfUtils.renderPdf(file, binding.imageView, object : PdfUtils.PdfRendererCallback {
                    override fun onRendered() {
                        binding.progressBar.visibility = android.view.View.GONE
                    }

                    override fun onFailure() {
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                })
            }

            override fun onFailure(message: String) {
                Log.d(TAG, "l> onFailure: $message")
            }
        })

    }
}