package com.e.got_compagnon

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class articleActivity() : AppCompatActivity(){
    private lateinit var  webView: WebView
    private lateinit var url: String
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        webView = findViewById(R.id.article_webview)
        webView.settings.setJavaScriptEnabled(true)
        val b = intent.extras
        if (b != null) {
            url = b.getString("url", "")
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String
            ): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        Log.i("URL", url)
        webView.loadUrl((url))
    }
}