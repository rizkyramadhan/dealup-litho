package id.dealup.dealup.rolesiswa.components

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.widget.Text
import org.json.JSONObject


@LayoutSpec
object PengumumanTabDetailSpec {
    @OnCreateLayout
    fun onCreateLayout(
        c: ComponentContext,
        @Prop json: String
    ): Component {
        val item: JSONObject = JSONObject(json)

        return Column.create(c)
            .child(Text.create(c).text("Pengumuman Detail").textSizeDip(20f))
            .build()
    }
}