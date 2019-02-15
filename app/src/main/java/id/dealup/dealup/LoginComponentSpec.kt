package id.dealup.dealup

import android.graphics.Color
import android.text.InputType
import android.view.View
import com.facebook.litho.*
import com.facebook.litho.animation.AnimatedProperties
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.Image
import com.facebook.litho.widget.TextChangedEvent
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import id.dealup.dealup.ui.UIButton
import id.dealup.dealup.ui.UIInput
import id.dealup.dealup.ui.UILoading
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

@LayoutSpec
object LoginComponentSpec {
    @OnCreateInitialState
    fun createInitialState(
        c: ComponentContext,
        focused: StateValue<Boolean>,
        username: StateValue<String>,
        password: StateValue<String>,
        usernameError: StateValue<String>,
        passwordError: StateValue<String>
    ) {
        focused.set(false)
        username.set("")
        password.set("")
        usernameError.set("")
        passwordError.set("")
    }

    @OnCreateTransition
    fun onCreateTransition(c: ComponentContext): Transition {
        return Transition
            .create(Transition.TransitionKeyType.GLOBAL, "logoPaddingTop")
            .animate(AnimatedProperties.SCALE, AnimatedProperties.HEIGHT, AnimatedProperties.SCALE_Y)
            .animator(Transition.timing(500));
    }

    @OnCreateLayout
    fun onCreateLayout(
        c: ComponentContext,
        @State loading: Boolean,
        @State username: String,
        @State password: String,
        @State usernameError: String,
        @State passwordError: String,
        @State focused: Boolean
    ): Component =
        Column.create(c)
            .child(
                Row.create(c)
                    .child(
                        Image
                            .create(c)
                            .drawableRes(R.drawable.logo)
                    )
                    .backgroundColor(Color.RED)
                    .transitionKey("logoPaddingTop")
                    .transitionKeyType(Transition.TransitionKeyType.GLOBAL)
                    .heightDip(if (focused) 90f else 170f)
                    .paddingDip(YogaEdge.TOP, if (focused) 30f else 90f)
                    .justifyContent(YogaJustify.CENTER)
                    .alignItems(YogaAlign.CENTER)
            )
            .child(
                UIInput.create(c)
                    .positionDip(YogaEdge.ALL, 0f)
                    .label("Username ")
                    .text(username)
                    .errorText(usernameError)
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .focusChangedEventHandler(LoginComponent.onFocusChanged(c))
                    .textChangedEventHandler(LoginComponent.onUsernameChanged(c))
                    .paddingDip(YogaEdge.TOP, if (focused) 20f else 40f)
                    .paddingDip(YogaEdge.BOTTOM, 10f)
            )
            .child(
                UIInput.create(c)
                    .label("Password")
                    .text(password)
                    .errorText(passwordError)
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .focusChangedEventHandler(LoginComponent.onFocusChanged(c))
                    .textChangedEventHandler(LoginComponent.onPasswordChanged(c))
                    .paddingDip(YogaEdge.BOTTOM, 45f)
            )
            .child(
                Row.create(c)
                    .child(
                        if (!loading)
                            UIButton.create(c)
                                .label("Login")
                                .widthDip(140f)
                                .heightDip(45f)
                                .clickHandler(LoginComponent.onLoginClick(c))
                        else
                            UILoading.create(c)
                    )
                    .justifyContent(YogaJustify.CENTER)
            )
            .paddingDip(YogaEdge.ALL, 20f)
            .positionDip(YogaEdge.ALL, 0f)
            .build()

    @OnUpdateState
    fun updateFocused(focused: StateValue<Boolean>, @Param focus: Boolean) {
        focused.set(focus)
    }

    @OnUpdateState
    fun updatePassword(password: StateValue<String>, @Param view: android.widget.EditText, @Param text: String) {
        password.set(text)
    }

    @OnUpdateState
    fun updateUsername(username: StateValue<String>, @Param view: android.widget.EditText, @Param text: String) {
        var newText = text
        if (text.length == 3 && username.get()!!.length < 3) {
            newText = text + " - "
            view.setText(newText)
            view.setSelection(view.text.length)
        }

        if (text.length == 4 && username.get()!!.length == 3) {
            newText = text.substring(0, 3) + " - " + text.substring(3)
            view.setText(newText)
            view.setSelection(view.text.length)
        }
        username.set(newText)
    }

    @OnUpdateState
    fun updateUsernameError(usernameError: StateValue<String>, @Param error: String) {
        usernameError.set(error)
    }

    @OnUpdateState
    fun updatePasswordError(passwordError: StateValue<String>, @Param error: String) {
        passwordError.set(error)
    }

    @OnUpdateState
    fun updateLoading(loading: StateValue<Boolean>, @Param isLoading: Boolean) {
        loading.set(isLoading)
    }

    @OnEvent(ClickEvent::class)
    fun onLoginClick(c: ComponentContext, @State username: String, @State password: String) {
        LoginComponent.updateLoading(c, true)

        doAsync {

            uiThread {
            }
        }
    }

    @OnEvent(FocusChangedEvent::class)
    fun onFocusChanged(c: ComponentContext, @FromEvent view: View, @FromEvent hasFocus: Boolean) {
        LoginComponent.updateFocused(c, hasFocus)
    }

    @OnEvent(TextChangedEvent::class)
    fun onUsernameChanged(c: ComponentContext, @FromEvent view: android.widget.EditText, @FromEvent text: String) {
        LoginComponent.updateUsername(c, view, text)
    }

    @OnEvent(TextChangedEvent::class)
    fun onPasswordChanged(c: ComponentContext, @FromEvent view: android.widget.EditText, @FromEvent text: String) {
        LoginComponent.updatePassword(c, view, text)
    }


}