package id.dealup.dealup.rolesiswa.components

import com.facebook.litho.annotations.FromEvent
import com.facebook.litho.annotations.OnEvent
import com.facebook.litho.annotations.Prop
import com.facebook.litho.annotations.ResType
import com.facebook.litho.sections.Children
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.annotations.GroupSectionSpec
import com.facebook.litho.sections.annotations.OnCreateChildren
import com.facebook.litho.sections.common.DataDiffSection
import com.facebook.litho.sections.common.RenderEvent
import com.facebook.litho.sections.common.SingleComponentSection
import com.facebook.litho.widget.ComponentRenderInfo
import com.facebook.litho.widget.RenderInfo
import com.facebook.yoga.YogaEdge
import id.dealup.dealup.ui.UILoading
import org.json.JSONArray
import org.json.JSONObject

@GroupSectionSpec
object PengumumanTabSpec {
    @OnCreateChildren
    fun onCreateChildren(
        c: SectionContext,
        @Prop(resType = ResType.STRING) json: String,
        @Prop(resType = ResType.BOOL) loading: Boolean
    ): Children {
        return Children.create()
            .child(
                if (loading)
                    SingleComponentSection.create(c)
                        .component(UILoading.create(c).paddingDip(YogaEdge.ALL, 30f))
                else
                    DataDiffSection.create<JSONObject>(c)
                        .data(generateData(json))
                        .renderEventHandler(PengumumanTab.onRender(c))
            )
            .build()
    }

    @OnEvent(RenderEvent::class)
    fun onRender(c: SectionContext, @FromEvent model: JSONObject?): RenderInfo {
        return ComponentRenderInfo.create()
            .component(
                PengumumanTabItem.create(c)
                    .json(model!!.toString())
                    .build()
            )
            .build()
    }

    private fun generateData(json: String): ArrayList<JSONObject> {
        val listdata = ArrayList<JSONObject>()
        if (!json.startsWith("["))
            return listdata;

        val pengumuman = JSONArray(json)
        if (pengumuman != null) {
            val jArray = pengumuman as JSONArray
            if (jArray != null) {
                for (i in 0 until jArray.length()) {
                    listdata.add(jArray.getJSONObject(i))
                }
            }
        }
        return listdata
    }
}