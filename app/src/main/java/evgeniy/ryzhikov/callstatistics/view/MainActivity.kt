package evgeniy.ryzhikov.callstatistics.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.ActivityMainBinding
import evgeniy.ryzhikov.callstatistics.view.fragments.UpdateDBFragment
import evgeniy.ryzhikov.callstatistics.view.fragments.HomeFragment
import evgeniy.ryzhikov.callstatistics.utils.HideNavigationBars
import evgeniy.ryzhikov.callstatistics.view.fragments.IncomingFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        addMenuClickListeners()
        checkPermissionAndStartFragment()
        HideNavigationBars.hide(window, binding.root)
    }

    private fun addMenuClickListeners() {
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itemMainPage -> {
                    startFragment(HomeFragment())
                    true
                }
                R.id.itemIncoming -> {
                    startFragment(IncomingFragment())
                    true
                }

                else -> return@setOnItemSelectedListener true
            }
        }
    }

    fun startFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment)
            .commit()
    }

    private fun checkPermissionAndStartFragment() {
        if (checkPermission()) {
            startFragment(UpdateDBFragment())
        } else {
            requestPermission()
        }
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

        when(requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startFragment(UpdateDBFragment())
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123456
    }
}