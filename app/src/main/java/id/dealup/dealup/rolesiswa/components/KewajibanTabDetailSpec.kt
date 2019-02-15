package id.dealup.dealup.rolesiswa.components

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import id.dealup.dealup.libs.Api
import id.dealup.dealup.libs.Store
import id.dealup.dealup.rolesiswa.XenditActivity
import id.dealup.dealup.ui.UIButton
import id.dealup.dealup.ui.UILoading
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


@LayoutSpec
object KewajibanTabDetailSpec {
    @OnCreateLayout
    fun onCreateLayout(
        c: ComponentContext,
        @State loading: Boolean,
        @Prop json: String
    ): Component {
        val item: JSONObject = JSONObject(json)
        val lunas = item.getString("lunas")
        return Column.create(c)
            .child(
                Text.create(c)
                    .text(item.getString("nama_kewajiban"))
                    .textSizeDip(30f)
                    .paddingDip(YogaEdge.BOTTOM, 10f)
            )
            .child(
                Row.create(c)
                    .child(Text.create(c).text("Nominal:").textSizeDip(20f))
                    .child(Text.create(c).text(item.getString("nominal_text")).textSizeDip(20f))
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .child(
                Row.create(c)
                    .child(Text.create(c).text("Periode:").textSizeDip(20f))
                    .child(Text.create(c).text(item.getString("periode")).textSizeDip(20f))
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .child(
                Row.create(c)
                    .child(Text.create(c).text("Batas Pembayaran:").textSizeDip(20f))
                    .child(Text.create(c).text(item.getString("batas_pembayaran")).textSizeDip(20f))
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .child(
                Row.create(c)
                    .child(Text.create(c).text("Status:").textSizeDip(20f))
                    .child(Text.create(c).text(item.getString("lunas")).textSizeDip(20f))
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .child(
                if (lunas == "Belum Lunas")
                    if (!loading)
                        Row.create(c)
                            .child(
                                UIButton.create(c)
                                    .label("Lakukan Pembayaran")
                                    .widthDip(260f)
                                    .heightDip(60f)
                                    .clickHandler(KewajibanTabDetail.onPembayaranClick(c, item))
                            )
                            .marginDip(YogaEdge.TOP, 30f)
                            .justifyContent(YogaJustify.CENTER)
                    else
                        UILoading.create(c)
                            .marginDip(YogaEdge.TOP, 30f)
                else null
            )
            .paddingDip(YogaEdge.ALL, 15f)
            .build()
    }

    @OnUpdateState
    fun updateLoading(loading: StateValue<Boolean>, @Param isLoading: Boolean) {
        loading.set(isLoading)
    }

    @OnEvent(ClickEvent::class)
    fun onPembayaranClick(c: ComponentContext, @Param it: JSONObject) {
        val sessionId = Api.sessionId
        val murid = Store.load("murid")
        val sekolah = Store.load("sekolah")
        val fmt = SimpleDateFormat("yyyy-MM-dd")

        KewajibanTabDetail.updateLoading(c, true)

        doAsync {
            val res = khttp.post(
                url = Api.baseUrl + "/api/Postgre?p=pembayaran",
                headers = mapOf("Authorization" to "Bearer " + sessionId),
                json = mapOf(
                    "kewajiban_id" to it.getInt("id"),
                    "email" to "j@dealup.id",
                    "judul" to it.getString("nama_kewajiban"),
                    "lunas" to "n",
                    "murid_id" to murid!!.getInt("id"),
                    "sekolah_id" to sekolah?.getInt("id"),
                    "status" to " Belum Dibayar ",
                    "tgl_invoice" to fmt.format(Date()),
                    "total" to it.getInt("nominal"),
                    "__tableExists" to false
                )
            )

            uiThread {
                if (res.statusCode > 300) {
                    Toast.makeText(c.androidContext, res.text, Toast.LENGTH_LONG).show()
                } else {
                    try {
                        val xenditInvoiceStr = res.jsonObject.getString("xendit_invoice")
                        if (xenditInvoiceStr != null) {
                            val xenditInvoice = JSONObject(xenditInvoiceStr)
                            val url = xenditInvoice.getString("invoice_url")
                            val intent = Intent(c.androidContext, XenditActivity::class.java)
                            intent.putExtra("url", url)
                            c.androidContext.startActivity(intent)
                            (c.androidContext as Activity).finish()
                        }
                    } catch (e: JSONException) {
                        KewajibanTabDetail.updateLoading(c, false)
                        Toast.makeText(c.androidContext, "Pembayaran gagal, mohon coba lagi...", Toast.LENGTH_LONG)
                            .show()
                        Log.e(KewajibanTabDetail::class.java.simpleName, res.text)
                        Log.e(KewajibanTabDetail::class.java.simpleName, e.toString())
                    }
                }
            }
        }
    }
}