package evgeniy.ryzhikov.callstatistics.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ActionBarBinding

class ActionBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?) : ConstraintLayout(context, attributeSet) {
    private var _binding: ActionBarBinding? = null
    private val binding: ActionBarBinding get() = _binding!!
    var isAnimate = true

    init {
        _binding = ActionBarBinding.bind(LayoutInflater.from(context).inflate(R.layout.action_bar, this))
        prepareAnimate()
        createMenu()
    }

    private fun createMenu() {
        binding.menu.setOnClickListener {
            showMenu(it, R.menu.action_bar_menu)
        }
    }

    private fun prepareAnimate() {
        doOnLayout {
            if (isAnimate) {
                startAnimate()
            }
        }
    }

    fun setContent(
        caption: String? = null,
        topName: String? = null,
        topValue: String? = null,
        bottomName: String? = null,
        bottomValue: String? = null,
        subhead: String? = null
    ) {
        if (caption != null) setTextAndVisible(binding.caption, caption)
        if (topName != null) setTextAndVisible(binding.topName, topName)
        if (topValue != null) setTextAndVisible(binding.topValue, topValue)
        if (bottomName != null) setTextAndVisible(binding.bottomName, bottomName)
        if (bottomValue != null) setTextAndVisible(binding.bottomValue, bottomValue)
        if (subhead != null) setTextAndVisible(binding.subhead, subhead)
    }

    private fun setTextAndVisible(textView: TextView, value: String) {
        textView.apply {
            text = value
            isVisible = true
        }
    }

    private fun startAnimate() {
        animation = AnimationUtils.loadAnimation(context, R.anim.fall_from_top)
        animate()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
           return@setOnMenuItemClickListener true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}