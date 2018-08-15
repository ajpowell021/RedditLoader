package powell.adam.redditloader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView


class WebViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.web_view_activity)

        val link = intent.getStringExtra("LINK")
        val webView = findViewById(R.id.web_view) as WebView

        webView.loadUrl(link)
    }
}
