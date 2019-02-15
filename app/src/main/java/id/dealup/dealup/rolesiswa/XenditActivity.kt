package id.dealup.dealup.rolesiswa

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_xendit.*


class XenditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(id.dealup.dealup.R.layout.activity_xendit)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        title = "Loading..."

        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                title = "Pembayaran"
            }
        };
        webview.settings.setJavaScriptEnabled(true);
        webview.settings.setDomStorageEnabled(true);
        webview.loadUrl(intent.getStringExtra("url"));
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
