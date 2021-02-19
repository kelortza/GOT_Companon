package com.e.got_compagnon.activity

import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.e.got_compagnon.Constants
import com.e.got_compagnon.R
import com.e.got_compagnon.model.OAuthTokensResponse
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.*
import java.io.IOException


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
                        getAccessTokens(it)
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
        /*
        POST https://id.twitch.tv/oauth2/token
            ?client_id=<your client ID>
            &client_secret=<your client secret>
            &code=<authorization code received above>
            &grant_type=authorization_code
            &redirect_uri=<your registered redirect URI>
         */
        //val client = OkHttpClient()

        //configure json
        val jsonConfig = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }

        val httpClient = HttpClient(OkHttp){
            install(JsonFeature) {
                serializer = KotlinxSerializer(jsonConfig)
            }
        }

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                try {
                    val response: OAuthTokensResponse =
                        httpClient.post<OAuthTokensResponse>("https://id.twitch.tv/oauth2/token") {
                            parameter("client_id", Constants.OAUTH_CLIENT_ID)
                            parameter("client_secret", Constants.OAUTH_CLIENT_SECRET)
                            parameter("code", authorizationCode)
                            parameter("grant_type", "authorization_code")
                            parameter("redirect_uri", Constants.OAUTH_REDIRECT_URI)
                        }
                    Log.i(TAG, "Got reponse from Twitch: $response")
                    //save access token
                    com.e.got_compagnon.service.UserManager(this@TwitchLoginActivity)
                        .saveAccessToken(response.accessToken)

                    //close
                    finish()
                    /*
                val formBody: RequestBody = FormBody.Builder()
                    .build()

                val request: Request = Request.Builder()
                    .url("https://id.twitch.tv/oauth2/token")
                    .post(formBody)
                    .build()

                try {
                    val response = client.newCall(request).execute().toString()
                    Log.i(TAG, "Got response from twitch: ${response.toString()}")
                    // Do something with the response.
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.i(TAG, "ha habido un problema")
                }

                 */
                } catch (t: Throwable){
                    //TODO: handle error
                }
            }
        }
    }

    private fun initViews() {
        webView = findViewById<WebView>(R.id.twitchWebView)
    }
}