package id.dealup.dealup.ui

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.EditText
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.TextChangedEvent
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import id.dealup.dealup.R
import id.dealup.dealup.libs.Store


@LayoutSpec(events = [TextChangedEvent::class, FocusChangedEvent::class])
object UIInputSpec {
    @OnCreateLayout
    internal fun onCreateLayout(
        c: ComponentContext,
        @Prop(resType = ResType.STRING) label: String,
        @Prop(resType = ResType.STRING) text: String,
        @Prop(resType = ResType.STRING) errorText: String,
        @Prop(resType = ResType.INT, optional = true) inputType: Int,
        @State labelColor: Int
    ): Component {
        var edInputType = inputType

        return Column.create(c)
            .child(
                Row.create(c)
                    .child(
                        Text
                            .create(c)
                            .paddingDip(YogaEdge.LEFT, 3f)
                            .textSizeDip(16f)
                            .textColor(labelColor)
                            .text(label)
                    )
                    .child(
                        Text
                            .create(c)
                            .paddingDip(YogaEdge.RIGHT, 3f)
                            .textSizeDip(16f)
                            .textColor(Color.RED)
                            .text(errorText)
                    )
                    .justifyContent(YogaJustify.SPACE_BETWEEN)
            )
            .child(
                Row.create(c)
                    .child(
                        EditText
                            .create(c)
                            .flexGrow(1f)
                            .textSizeDip(18f)
                            .text(text)
                            .inputType(edInputType)
                            .minLines(1)
                            .maxLines(1)
                            .focusChangeHandler(UIInput.onFocusChanged(c))
                            .isSingleLine(true)
                            .textChangedEventHandler(UIInput.onTextChanged(c))
                    )
                    .heightDip(50f)
            )
            .positionDip(YogaEdge.ALL, 0f)
            .build()
    }

    @OnCreateInitialState
    fun createInitialState(
        c: ComponentContext,
        labelColor: StateValue<Int>
    ) {
        labelColor.set(Color.GRAY)
    }


    @OnUpdateState
    fun updateLabelColor(labelColor: StateValue<Int>, @Param color: Int) {
        labelColor.set(color)
    }

    @OnEvent(FocusChangedEvent::class)
    fun onFocusChanged(c: ComponentContext, @FromEvent view: View, @FromEvent hasFocus: Boolean) {
        val handler = UIInput.getFocusChangedEventHandler(c)
        if (hasFocus) {
            UIInput.updateLabelColor(c, ContextCompat.getColor(Store.context, R.color.colorAccent))
        } else {
            UIInput.updateLabelColor(c, Color.GRAY)
        }
        if (handler != null) {
            UIInput.dispatchFocusChangedEvent(handler, view, hasFocus)
        }
    }

    @OnEvent(com.facebook.litho.widget.TextChangedEvent::class)
    fun onTextChanged(c: ComponentContext, @FromEvent view: android.widget.EditText, @FromEvent text: String) {
        val handler = UIInput.getTextChangedEventHandler(c)
        if (handler != null) {
            UIInput.dispatchTextChangedEvent(handler, view, text)
        }
    }

}
