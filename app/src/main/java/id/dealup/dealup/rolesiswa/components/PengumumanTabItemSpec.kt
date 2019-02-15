package id.dealup.dealup.rolesiswa.components

import android.graphics.Color
import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.annotations.ResType
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaEdge
import org.json.JSONObject

@LayoutSpec
object PengumumanTabItemSpec {
    @OnCreateLayout
    fun onCreateLayout(
        c: ComponentContext,
        @Prop(resType = ResType.STRING) json: String
    ): Component {
        val item = JSONObject(json)
        val tgl_ = item.getString("tgl").split("T")[0].split("-")
        val strMonths = arrayOf(
            "Januari",
            "Februari",
            "Maret",
            "April",
            "Mei",
            "Juni",
            "Juli",
            "Agustus",
            "September",
            "oktober",
            "November",
            "Desember"
        )
        val tgl = "${tgl_[2]} ${strMonths[tgl_[1].toInt() - 1]} ${tgl_[0]}"

        return Column.create(c)
            .child(
                Text.create(c)
                    .text(tgl).textSizeDip(16f).textColor(Color.GRAY)
            )
            .child(
                Text.create(c)
                    .text(item.getString("pengumuman")).textSizeDip(20f)
            )
            .paddingDip(YogaEdge.ALL, 15f)
            .build()
    }

}