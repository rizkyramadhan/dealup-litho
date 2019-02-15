package id.dealup.dealup.rolesiswa.components

import android.content.Intent
import android.graphics.Color
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import id.dealup.dealup.rolesiswa.TabDetailActivity
import org.json.JSONObject
import java.text.DateFormatSymbols
import java.util.*


@LayoutSpec
object KewajibanTabItemSpec {
    @OnCreateLayout
    fun onCreateLayout(
        c: ComponentContext,
        @Prop(resType = ResType.STRING) json: String
    ): Component {
        val item = JSONObject(json)
        val nominal = "Rp " + String.format("%,.0f", item.getString("nominal").toFloat())

        // parse lunas
        var lunas = false
        val invoices = item.getJSONArray("invoice")
        if (invoices.length() > 0) {
            val invoice = invoices.getJSONObject(0)
            if (invoice.getString("lunas") == "Sudah Lunas") {
                lunas = true
            }
        }

        // parse periode
        var periode = ""
        if (item.getString("tipe_pembayaran") == "Bulanan") {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val months = DateFormatSymbols().getMonths()
            periode = "${months[month]} ${year}"
        }
        item.put("nominal_text", nominal)
        item.put("lunas", if (lunas) "Sudah Lunas" else "Belum Lunas")
        item.put("periode", periode)

        return Column.create(c)
            .clickHandler(KewajibanTabItem.onItemClick(c, item.toString()))
            .child(
                Row.create(c)
                    .child(
                        Column.create(c)
                            .child(
                                Text.create(c)
                                    .text(item.getString("nama_kewajiban")).textSizeDip(20f)
                                    .marginDip(YogaEdge.BOTTOM, 5f)
                            )
                            .child(
                                Text.create(c)
                                    .textColor(Color.DKGRAY)
                                    .text(item.getString("tipe_pembayaran")).textSizeDip(18f)
                                    .paddingDip(YogaEdge.LEFT, 5f)
                                    .paddingDip(YogaEdge.RIGHT, 5f)
                                    .border(
                                        Border.create(c)
                                            .widthDip(YogaEdge.ALL, 1f)
                                            .color(YogaEdge.ALL, Color.DKGRAY)
                                            .radiusDip(5f)
                                            .build()
                                    )
                            )
                    )
                    .child(
                        Column.create(c)
                            .child(
                                Text.create(c)
                                    .textColor(Color.DKGRAY)
                                    .text(nominal).textSizeDip(18f)
                                    .marginDip(YogaEdge.BOTTOM, 5f)
                            )
                            .child(
                                if (!lunas)
                                    Text.create(c)
                                        .textColor(Color.RED)
                                        .text("✗ Belum Lunas").textSizeDip(17f)
                                        .paddingDip(YogaEdge.LEFT, 5f)
                                        .paddingDip(YogaEdge.RIGHT, 5f)
                                        .border(
                                            Border.create(c)
                                                .widthDip(YogaEdge.ALL, 1f)
                                                .color(YogaEdge.ALL, Color.RED)
                                                .radiusDip(5f)
                                                .build()
                                        )
                                else
                                    Text.create(c).textColor(0xFF8363FF.toInt())
                                        .text("✓ Sudah Lunas").textSizeDip(17f)
                                        .paddingDip(YogaEdge.LEFT, 5f)
                                        .paddingDip(YogaEdge.RIGHT, 5f)
                                        .border(
                                            Border.create(c)
                                                .widthDip(YogaEdge.ALL, 1f)
                                                .color(YogaEdge.ALL, 0xFF8363FF.toInt())
                                                .radiusDip(5f)
                                                .build()
                                        )
                            )
                            .alignItems(YogaAlign.FLEX_END)
                    )
                    .alignItems(YogaAlign.CENTER)
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .paddingDip(YogaEdge.ALL, 10f)
            .build()
    }

    @OnEvent(ClickEvent::class)
    fun onItemClick(c: ComponentContext, @Param json: String) {
        val intent = Intent(c.androidContext, TabDetailActivity::class.java)
        intent.putExtra("mode", "kewajiban")
        intent.putExtra("json", json)
        c.androidContext.startActivity(intent)
    }
}