package com.e.got_compagnon.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.e.got_compagnon.Constants
import com.e.got_compagnon.R

class TwitchLoginActivity : AppCompatActivity() {
    private val TAG = "TwitchLoginActivity"
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitch_login)
        initViews()
        loadOAuthUrl()
    }

    private fun loadOAuthUrl() {
        //1 - Prepare URL
        val uri = Uri.parse("https://id.twitch.tv/oauth2/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", Constants.OAUTH_CLIENT_ID)
            .appendQueryParameter("redirect_uri", Constants.OAUTH_REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", listOf("user:edit", "user:read:email").joinToString(" "))
            .build()

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request?.url?.toString()?.startsWith(Constants.OAUTH_REDIRECT_URI) == true){
                    //Login success
                    Log.i(TAG, "Login success with URL: ${request?.url}")
                    request.url.getQueryParameter("code")?.let {
                        Log.i(TAG, "Got authentification CODE $it")
                        //exchange code+ client_secret -> access token
                        webView.visibility = View.GONE
                        //getAccessTokens()
                    } ?: run {
                        //TODO: Handle error
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        Log.i(TAG, "Starting login with URL: ${uri.toString()}")
        //2 - Load URL
        webView.loadUrl(uri.toString())
    }

    private fun getAccessTokens(authorizationCode: String){

    }

    private fun initViews() {
        webView = findViewById<WebView>(R.id.twitchWebView)
    }
}