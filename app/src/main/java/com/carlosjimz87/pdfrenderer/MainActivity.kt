package com.carlosjimz87.pdfrenderer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carlosjimz87.pdfrenderer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), WebUtils.ListenerWebView {
    private val PDF_URL= "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf"

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
           if(binding.webView.progress < 100) {
              binding.webView.stopLoading()
               Log.d(TAG, "Timeout error")
           }
        }

        binding.webView.let { WebUtils.initializeWebView(it, this) }
        loadPdf(PDF_URL)

        binding.btnReload.setOnClickListener {
            loadPdf(PDF_URL)
        }
    }

    private fun loadPdf(url: String) {
        binding.webView.loadUrl(url)
//        binding.webView.loadUrl("googlechrome://navigate?url=$url")
    }

    override fun onPageStarted(url: String) {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        handler.postDelayed(runnable, 15000)
    }

    override fun onPageFinished(url: String) {
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        handler.removeCallbacks(runnable)
    }

    override fun doUpdateVisitedHistory(url: String) {
    }

    override fun onReceivedError(error: String) {
        Toast.makeText(this, "error $error", Toast.LENGTH_SHORT).show()
    }
}