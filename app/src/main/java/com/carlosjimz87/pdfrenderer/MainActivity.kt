package com.carlosjimz87.pdfrenderer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carlosjimz87.pdfrenderer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), WebUtils.ListenerWebView {
    private val PDF_URL2= "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf"
    private val PDF_URL= "https://www.adobe.com/"
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.webView.let { WebUtils.initializeWebView(it, this) }
        loadPdf(PDF_URL)

        binding.btnReload.setOnClickListener {
            loadPdf(PDF_URL)
        }
    }

    private fun loadPdf(url: String) {
        Log.d(TAG, "loadingPdf: $url")
        binding.webView.loadUrl(url)
//        binding.webView.loadUrl("googlechrome://navigate?url=$url")
    }

    override fun onPageStarted(url: String) {
        Toast.makeText(this, "page started on $url", Toast.LENGTH_SHORT).show()
    }

    override fun onPageFinished(url: String) {
        Toast.makeText(this, "page finished on $url", Toast.LENGTH_SHORT).show()
    }

    override fun doUpdateVisitedHistory(url: String) {
        Toast.makeText(this, "visited history on $url", Toast.LENGTH_SHORT).show()
    }

    override fun onReceivedError(error: String) {
        Toast.makeText(this, "error $error", Toast.LENGTH_SHORT).show()
    }
}