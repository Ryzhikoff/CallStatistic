package evgeniy.ryzhikov.callstatistics.ui.customview

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.PopUpDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PopUpDialog private constructor(
    private val title: String? = null,
    private val content: String = "",
    private val rightButtonText: String? = null,
    private val rightButtonListener: PopUpDialogClickListener? = null,
    private val leftButtonText: String? = null,
    private val leftButtonListener: PopUpDialogClickListener? = null,
    private val duration: Long? = null,
    private val animationType: AnimationType? = null
) : DialogFragment(R.layout.pop_up_dialog) {

    private var onCanceledListener: OnCanceledListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        PopUpDialogBinding.bind(view).apply {
            title.apply {
                if (this@PopUpDialog.title == null) {
                    this.isVisible = false
                } else {
                    text = this@PopUpDialog.title
                }
            }

            content.text = this@PopUpDialog.content

            leftButton.apply {
                if (leftButtonText == null) {
                    this.isVisible = false
                } else {
                    text = leftButtonText
                    background = AppCompatResources.getDrawable(requireContext(), R.drawable.pop_up_button_background)
                }
            }
            rightButton.apply {
                if (this@PopUpDialog.rightButtonText == null) {
                    this.isVisible = false
                } else {
                    text = rightButtonText
                    background = AppCompatResources.getDrawable(requireContext(), R.drawable.pop_up_button_background)
                }
            }

            leftButton.setOnClickListener {
                leftButtonListener?.onClick(this@PopUpDialog)
            }

            rightButton.setOnClickListener {
                rightButtonListener?.onClick(this@PopUpDialog)
            }

            if (duration != null) {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(duration)
                    this@PopUpDialog.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            when (animationType) {
                AnimationType.BOTTOM -> {
                    decorView.setPadding(0, 0, 0, 100.toPx)
                    setGravity(Gravity.BOTTOM)
                    setWindowAnimations(R.style.pop_up_dialog_from_bottom)
                }

                AnimationType.RIGHT -> {
                    setWindowAnimations(R.style.pop_up_dialog_from_right)
                }

                AnimationType.SPLASH -> {
                    setWindowAnimations(R.style.pop_up_dialog_splash)
                }

                null -> {}
            }

        }
    }

    override fun onCancel(dialog: DialogInterface) {
        onCanceledListener?.onCanceled()
        super.onCancel(dialog)
    }

    fun setOnCanceledListener(callback: () -> Unit) {
        onCanceledListener = object : OnCanceledListener {
            override fun onCanceled() {
                callback()
            }
        }
    }

    private interface OnCanceledListener {
        fun onCanceled()
    }

    interface PopUpDialogClickListener {
        fun onClick(popUpDialog: PopUpDialog)
    }

    companion object {
        const val TAG = "POP_UP_DIALOG"
    }

    data class Builder(
        var title: String? = null,
        var content: String = "",
        var rightButtonText: String? = null,
        var rightButtonListener: PopUpDialogClickListener? = null,
        var leftButtonText: String? = null,
        var leftButtonListener: PopUpDialogClickListener? = null,
        var duration: Long? = null,
        var animationType: AnimationType? = null
    ) {
        fun title(title: String) = apply { this.title = title }
        fun content(content: String) = apply { this.content = content }
        fun rightButtonText(rightButtonText: String) = apply { this.rightButtonText = rightButtonText }
        fun rightButtonListener(rightButtonListener: PopUpDialogClickListener) = apply { this.rightButtonListener = rightButtonListener }
        fun leftButtonText(leftButtonText: String) = apply { this.leftButtonText = leftButtonText }
        fun leftButtonListener(leftButtonListener: PopUpDialogClickListener) = apply { this.leftButtonListener = leftButtonListener }
        fun duration(millis: Long) = apply { this.duration = millis }
        fun animationType(animationType: AnimationType) = apply { this.animationType = animationType }
        fun build() = PopUpDialog(title, content, rightButtonText, rightButtonListener, leftButtonText, leftButtonListener, duration, animationType)
    }

    private val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    enum class AnimationType {
        BOTTOM,
        RIGHT,
        SPLASH
    }
}

//@Parcelize
//class PopUpDialogSettings private constructor(
//    private val title: String? = null,
//    private val content: String = "",
//    private val rightButtonText: String? = null,
//    private val rightButtonListener: PopUpDialog.PopUpDialogClickListener? = null,
//    private val leftButtonText: String? = null,
//    private val leftButtonListener: PopUpDialog.PopUpDialogClickListener? = null,
//    private val duration: Long? = null,
//    private val animationType: PopUpDialog.AnimationType? = null
//): Parcelable {
//    data class Builder(
//        var title: String? = null,
//        var content: String = "",
//        var rightButtonText: String? = null,
//        var rightButtonListener: PopUpDialog.PopUpDialogClickListener? = null,
//        var leftButtonText: String? = null,
//        var leftButtonListener: PopUpDialog.PopUpDialogClickListener? = null,
//        var duration: Long? = null,
//        var animationType: PopUpDialog.AnimationType? = null
//    ) {
//        fun title(title: String) = apply { this.title = title }
//        fun content(content: String) = apply { this.content = content }
//        fun rightButtonText(rightButtonText: String) = apply { this.rightButtonText = rightButtonText }
//        fun rightButtonListener(rightButtonListener: PopUpDialog.PopUpDialogClickListener) = apply { this.rightButtonListener = rightButtonListener }
//        fun leftButtonText(leftButtonText: String) = apply { this.leftButtonText = leftButtonText }
//        fun leftButtonListener(leftButtonListener: PopUpDialog.PopUpDialogClickListener) = apply { this.leftButtonListener = leftButtonListener }
//        fun duration(millis: Long) = apply { this.duration = millis }
//        fun animationType(animationType: PopUpDialog.AnimationType) = apply { this.animationType = animationType }
//        fun build() =
//            PopUpDialogSettings(title, content, rightButtonText, rightButtonListener, leftButtonText, leftButtonListener, duration, animationType)
//    }
//}