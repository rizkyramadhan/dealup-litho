package id.dealup.dealup.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.MountSpec
import com.facebook.litho.annotations.OnCreateMountContent
import com.facebook.litho.annotations.OnMount
import com.facebook.litho.annotations.Prop
import id.dealup.dealup.R
import id.dealup.dealup.libs.Store.Companion.context
import me.angrybyte.sillyandroid.extras.Coloring


@MountSpec
object UIButtonSpec {
    @OnCreateMountContent
    internal fun onCreateMountContent(c: Context): Button {
        return Button(c)
    }

    @OnMount
    internal fun onMount(c: ComponentContext, btn: Button, @Prop label: String) {
        btn.text = label
        btn.textSize = 18f

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            roundCorner(btn)
        };
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun roundCorner(btn: Button) {
        btn.setTextColor(Color.parseColor("#FFFFFF"));
        btn.setTransformationMethod(null)
        btn.stateListAnimator = null
        btn.background = Coloring.createRippleDrawable(
            ContextCompat.getColor(context, R.color.colorPrimary),
            ContextCompat.getColor(context, R.color.colorPrimaryDark),
            Rect(0, 0, 0, 0),
            15
        ) as Drawable?
    }
}
