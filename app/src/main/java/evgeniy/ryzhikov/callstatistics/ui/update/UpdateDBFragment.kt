package evgeniy.ryzhikov.callstatistics.ui.update

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentUpdateDbBinding
import evgeniy.ryzhikov.callstatistics.ui.MainActivity
import evgeniy.ryzhikov.callstatistics.ui.home.HomeFragment
import evgeniy.ryzhikov.callstatistics.ui.services.UpdateProgressLiveData
import evgeniy.ryzhikov.callstatistics.ui.services.UpdateDBService
import evgeniy.ryzhikov.callstatistics.ui.services.UpdateDBService.Companion.PROGRESS_MAX
import evgeniy.ryzhikov.callstatistics.ui.services.isServiceRunning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateDBFragment : Fragment(R.layout.fragment_update_db) {
    private var _binding: FragmentUpdateDbBinding? = null
    private val binding get() = _binding!!
    private val scopeLoad = CoroutineScope(Job())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUpdateDbBinding.bind(view)

        startLoadAnimation()
        updateProgressListener()
        updateForeground()
    }

    private fun startLoadAnimation() {
        binding.loadingAnimation.playAnimation()
        scopeLoad.launch(Dispatchers.Main) {
            var i = 0
            val texts = resources.getStringArray(R.array.update_db_text)
            while (true) {
                if (i > 3) i = 0
                binding.tvLoad.text = texts[i]
                i++
                delay(500)
            }
        }
    }

    private fun updateProgressListener() {
        UpdateProgressLiveData.serviceCompleted.observe(viewLifecycleOwner) { progress ->
            updateProgressBar(progress)
            println("progress $progress")
            if (progress == PROGRESS_MAX) {
                startHomeFragment()
            }
        }
    }

    private fun startHomeFragment() {
        scopeLoad.cancel()
        (requireActivity() as MainActivity).startFragment(HomeFragment())
        onDestroy()
    }

    private fun updateProgressBar(progress: Int) {
        binding.progressBar.setProgressPercentage(progress.toDouble(), true)
    }

    private fun updateForeground() {
        if (!isServiceRunning(requireContext(), UpdateDBService::class.java)) {
            val serviceIntent = Intent(requireContext(), UpdateDBService::class.java)
            requireContext().startForegroundService(serviceIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scopeLoad.cancel()
        _binding = null
    }
}