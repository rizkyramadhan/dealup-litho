package id.dealup.dealup.rolesiswa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import id.dealup.dealup.rolesiswa.components.KewajibanTabDetail
import id.dealup.dealup.rolesiswa.components.PengumumanTabDetail

class TabDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val mode = intent.getStringExtra("mode")
        val json = intent.getStringExtra("json")

        val component =
            if (mode == "kewajiban") KewajibanTabDetail
                .create(ComponentContext(this))
                .json(json)
                .build()
            else PengumumanTabDetail
                .create(ComponentContext(this))
                .json(json)
                .build()

        setContentView(LithoView.create(this, component))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
