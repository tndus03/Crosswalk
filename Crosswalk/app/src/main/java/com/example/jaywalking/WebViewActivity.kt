package com.example.jaywalking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.jaywalking.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding:ActivityWebViewBinding
    inner class KakaoJavaScriptInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            val intent = Intent()
            intent.putExtra("result", data)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_web_view)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.setDomStorageEnabled(true)
            addJavascriptInterface(KakaoJavaScriptInterface(), "Android")
            webViewClient = object:WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: String
                ): Boolean {
                    view.loadUrl(request)
                    return true
                }
            }
        }

        binding.webView.loadUrl("https://jaywalking-d91dc.web.app/daum3.html")
    }
}