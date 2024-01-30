package evgeniy.ryzhikov.callstatistics.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ActivityMainBinding
import evgeniy.ryzhikov.callstatistics.ui.customview.PopUpDialog


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            navController.setGraph(R.navigation.on_start_nav_graph)
            checkPermissionAndStartFragment()
        } else {
            startBottomNavigation()
        }

    }

    fun startBottomNavigation() {
        navController.setGraph(R.navigation.main_nav_graph)
        binding.bottomNavigationView.apply {
            isVisible = true
            setupWithNavController(navController)
        }
    }

    private fun checkPermissionAndStartFragment() {
        if (checkPermission()) {
            navController.navigate(R.id.action_splash_to_update_db)
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
            navController.navigate(R.id.action_splash_to_update_db)
        }
    }

    override fun onStop() {
        super.onStop()
        navController.saveState()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123456
        const val KEY_TYPE_CALL = "type_call"
    }
}