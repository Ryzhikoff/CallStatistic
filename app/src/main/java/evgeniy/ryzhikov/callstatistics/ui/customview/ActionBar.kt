package evgeniy.ryzhikov.callstatistics.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ActionBarBinding

class ActionBar @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private var _binding: ActionBarBinding? = null
    private val binding: ActionBarBinding get() = _binding!!
    var isAnimate = true
//    private var menuItemClickListener: MenuItemClickListener? = null

    init {
        _binding = ActionBarBinding.bind(LayoutInflater.from(context).inflate(R.layout.action_bar, this))
        prepareAnimate()
//        createMenu()
    }

    private fun prepareAnimate() {
        doOnLayout {
            if (isAnimate) {
                startAnimate()
            }
        }
    }

    private fun startAnimate() {
        animation = AnimationUtils.loadAnimation(context, R.anim.fall_from_top)
        animate()
    }

//    private fun createMenu() {
////        binding.menu.setOnClickListener {
////            showMenu(it, R.menu.action_bar_menu)
////        }
//    }
//
//    private fun showMenu(v: View, @MenuRes menuRes: Int) {
//        val popupMenu = PopupMenu(context!!, v)
//        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
//        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
//            menuItemClickListener?.itemClicked(menuItem)
//            return@setOnMenuItemClickListener true
//        }
//        popupMenu.show()
//    }
//
//    fun setMenuClickListener(listener: (MenuItem) -> Unit) {
//        menuItemClickListener = object : MenuItemClickListener {
//            override fun itemClicked(menuItem: MenuItem) {
//                listener(menuItem)
//            }
//        }
//    }

    fun setContent(
        caption: String? = null,
        topName: String? = null,
        topValue: String? = null,
        bottomName: String? = null,
        bottomValue: String? = null,
        subhead: String? = null
    ) {
        if (caption != null) setTextAndVisible(binding.caption, caption)
        if (topName != null) {
            binding.topArea.isVisible = true
            binding.topName.apply {
                isVisible = true
                setText(topName)
            }
        }
        if (topValue != null) setTextAndVisible(binding.topValue, topValue)
        if (bottomName != null) {
            binding.bottomArea.isVisible = true
            binding.bottomName.apply {
                isVisible = true
                setText(bottomName)
            }
        }
        if (bottomValue != null) setTextAndVisible(binding.bottomValue, bottomValue)
        if (subhead != null) setTextAndVisible(binding.subhead, subhead)
    }


    private fun setTextAndVisible(textView: TextView, value: String) {
        textView.apply {
            text = value
            isVisible = true
        }
    }

//    private interface MenuItemClickListener {
//        fun itemClicked(menuItem: MenuItem)
//    }

}