package id.dealup.dealup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.soloader.SoLoader
import id.dealup.dealup.libs.Store
import id.dealup.dealup.rolesiswa.TabActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoLoader.init(this, false)
        setContentView(R.layout.activity_main)
        start()
    }

    override fun onStart() {
        super.onStart()
        start()
    }

    fun start() {
        Store.context = this;
        val profile = Store.load("profile")
        if (profile == null) {
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
        } else {
            val intent = TabActivity.newIntent(this)
            startActivity(intent)
        }
    }
}
