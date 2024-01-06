package evgeniy.ryzhikov.callstatistics.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ActivityMainBinding
import evgeniy.ryzhikov.callstatistics.ui.customview.PopUpDialog
import evgeniy.ryzhikov.callstatistics.ui.update.UpdateDBFragment
import evgeniy.ryzhikov.callstatistics.ui.home.HomeFragment
import evgeniy.ryzhikov.callstatistics.ui.type_calls.TypeCallsFragment
import evgeniy.ryzhikov.callstatistics.ui.statistic.StatByPeriodFragment
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE
import evgeniy.ryzhikov.callstatistics.utils.OUTGOING_TYPE

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        addMenuClickListeners()
        checkPermissionAndStartFragment()
//        HideNavigationBars.hide(window, binding.root)
    }

    private fun addMenuClickListeners() {
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemMainPage -> {
                    startFragment(HomeFragment())
                    true
                }

                R.id.itemStatByPeriod -> {
                    startFragment(StatByPeriodFragment())
                    true
                }

                R.id.itemIncoming -> {
                    startFragment(TypeCallsFragment(INCOMING_TYPE))
                    true
                }

                R.id.itemOutgoing -> {
                    startFragment(TypeCallsFragment(OUTGOING_TYPE))
                    true
                }

                else -> return@setOnItemSelectedListener true
            }
        }
    }

    fun startFragment(fragment: Fragment, showBottomBar: Boolean = true) {
        binding.navigation.isVisible = showBottomBar
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment)
            .commit()
    }

    private fun checkPermissionAndStartFragment() {
        if (checkPermission()) {
            startFragment(UpdateDBFragment(), false)
        } else {
            showPopUpDialog(requestListener, getString(R.string.pop_up_launch_content))
        }
    }

    private val requestListener = object : PopUpDialog.PopUpDialogClickListener {
        override fun onClick(popUpDialog: PopUpDialog) {
            popUpDialog.dismiss()
            requestPermission()
        }
    }

    private fun showPopUpDialog(listener: PopUpDialog.PopUpDialogClickListener, content: String) {
        PopUpDialog.Builder()
            .title(getString(R.string.pop_up_launch_title))
            .content(content)
            .leftButtonText(getString(R.string.ok))
            .leftButtonListener(listener)
            .animationType(PopUpDialog.AnimationType.SPLASH)
            .build()
            .show(supportFragmentManager, PopUpDialog.TAG)
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALL_LOG
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CALL_LOG),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPopUpDialog(infoListener, getString(R.string.pop_up_info_content))
                } else {
                    finishAndRemoveTask()
                }
        }
    }

    private val infoListener = object : PopUpDialog.PopUpDialogClickListener {
        override fun onClick(popUpDialog: PopUpDialog) {
            popUpDialog.dismiss()
            startFragment(UpdateDBFragment(), false)
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123456
    }
}