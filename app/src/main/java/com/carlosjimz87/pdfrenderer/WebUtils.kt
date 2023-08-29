package com.carlosjimz87.pdfrenderer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient


class WebUtils {

    interface ListenerWebView {
        fun onPageStarted(url: String)
        fun onPageFinished(url: String)
        fun doUpdateVisitedHistory(url: String)
        fun onReceivedError(error: String)
    }

    companion object {

        /**
         * Method to initialize a webview with webclient, webChromeClient and JavaScript Support
         * @param webView
         */
        fun initializeWebView(webView: WebView, listenerWebView: ListenerWebView): WebView {
            initializeWebClient(webView, listenerWebView)
            initializeWebChromeClient(webView)
            configSettingsWebView(webView)

            return webView
        }

        @SuppressLint("SetJavaScriptEnabled")
        private fun configSettingsWebView(webView: WebView) {
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
        }

        /**
         * Method to initialize Webclient into a WebView
         * @param webView
         */
        private fun initializeWebClient(webView: WebView, listenerWebView: ListenerWebView) {
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    Log.d(TAG, "l> shouldOverrideUrlLoading${request.url.path}")
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    Log.d(TAG, "l> onPageStarted: $url")
                    super.onPageStarted(view, url, favicon)
                    listenerWebView.onPageStarted(url)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    Log.d(TAG, "l> onPageFinished: $url")
                    super.onPageFinished(view, url)
                    listenerWebView.onPageFinished(url)
                }

                override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                    Log.d(TAG, "l> doUpdateVisitedHistory: $url")
                    super.doUpdateVisitedHistory(view, url, isReload)
                    listenerWebView.doUpdateVisitedHistory(url)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    Log.e(TAG, "l> onReceivedError: (${error?.errorCode}) ${error?.description}")
                    super.onReceivedError(view, request, error)
                    listenerWebView.onReceivedError(error?.description.toString())
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    Log.e(TAG, "l> onReceivedHttpError: $errorResponse")
                    super.onReceivedHttpError(view, request, errorResponse)
                    listenerWebView.onReceivedError(errorResponse?.reasonPhrase.toString())
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    Log.e(TAG, "l> onReceivedSslError: $error")
                    super.onReceivedSslError(view, handler, error)
                    listenerWebView.onReceivedError("Ssl error on :${error?.url.toString()}")
                }

            }
        }

        /**
         * Method to initialize WebChromeClient into a WebView
         * @param webView
         */
        private fun initializeWebChromeClient(webView: WebView) {
            webView.webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
                    Log.d(TAG, "onCreateWindow")
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                }

                override fun onCloseWindow(window: WebView) {
                    Log.d(TAG, "onCloseWindow")
                    super.onCloseWindow(window)
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//                    Log.d(TAG, "onConsoleMessage ${consoleMessage?.message()}")
                    return super.onConsoleMessage(consoleMessage)

                }
            }
        }
    }
}