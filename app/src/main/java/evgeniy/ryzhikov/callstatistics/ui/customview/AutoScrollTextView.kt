package evgeniy.ryzhikov.callstatistics.ui.customview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.AutoScrollTextViewBinding
import kotlin.math.abs

class AutoScrollTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val binding: AutoScrollTextViewBinding =
        AutoScrollTextViewBinding.bind(inflate(context, R.layout.auto_scroll_text_view, this))

    private var animator: ValueAnimator? = null

    @StyleRes
    private var innerTextStyle = -1
    @ColorRes
    private var innerTextColor = -1
    private var innerTextSize = -1

    init {
        if (attrs != null) {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollTextView)

            innerTextStyle = typeArray.getResourceId(R.styleable.AutoScrollTextView_innerTextStyle, -1)
            innerTextColor = typeArray.getResourceId(R.styleable.AutoScrollTextView_innerTextColor, -1)
            innerTextSize = typeArray.getInt(R.styleable.AutoScrollTextView_innerTextSize, -1)

            typeArray.recycle()
        }
    }

    fun setText(text: CharSequence) = with(binding) {
        textView.apply {
            if (innerTextStyle != -1) setTextAppearance(innerTextStyle)
            if (innerTextColor != -1) setTextColor(ContextCompat.getColor(context, innerTextColor))
            if (innerTextSize != -1) textSize = innerTextSize.toFloat()
            this.text = text

            post {
                this@AutoScrollTextView.doOnGlobalLayout {
                    println("this@AutoScrollTextView.doOnGlobalLayout")
                    if (this@AutoScrollTextView.width < textView.width) {
                        println("this@AutoScrollTextView.width ${this@AutoScrollTextView.width} binding.textView.width ${textView.width}")
                        this@AutoScrollTextView.doOnGlobalLayout { width ->
                            loopAnimate(textView = textView, fixedScrollWidth = width)
                        }
                    }
                }
            }
        }
    }

    private fun loopAnimate(textView: TextView, fixedScrollWidth: Int) {
        val scrollDistance = textView.width - fixedScrollWidth
        val duration = abs(scrollDistance * 30)
        animateToEnd(scrollDistance, duration)
    }

    private fun animateToEnd(scrollDistance: Int, duration: Int) {
        animator = ValueAnimator.ofInt(0, scrollDistance).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                this@AutoScrollTextView.scrollTo(value, 0)
            }

            doOnEnd {
                println("!!! animateToEnd.doOnEnd text - ${binding.textView.text}")
                animateToStart(scrollDistance, duration)
            }
            this.duration = duration.toLong()
            start()
        }
    }

    private fun animateToStart(scrollDistance: Int, duration: Int) {
        animator = ValueAnimator.ofInt(scrollDistance, 0).apply {
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                this@AutoScrollTextView.scrollTo(value, 0)
            }

            doOnEnd {
                println("!!! animateToStart.doOnEnd text - ${binding.textView.text}")
                animateToEnd(scrollDistance, duration)
            }
            this.duration = duration.toLong()
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.apply {
            removeAllListeners()
            cancel()
        }
    }
}

fun HorizontalScrollView.doOnGlobalLayout(action: (Int) -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val newWidth = this@doOnGlobalLayout.width
            action(newWidth)

            this@doOnGlobalLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}
