package com.gohar_amin.tz.acitivities

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.gohar_amin.tz.R


class HelpAndSupportActivity : AppCompatActivity() {
    lateinit var webView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_and_support)
        webView=findViewById(R.id.webView)
        webView.settings.javaScriptEnabled=true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://www.google.com");
    }
}