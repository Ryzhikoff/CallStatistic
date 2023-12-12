package evgeniy.ryzhikov.callstatistics.view.fragments

import android.database.Cursor
import android.os.Bundle
import android.provider.CallLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentUpdateDbBinding
import evgeniy.ryzhikov.callstatistics.ui.MainActivity
import evgeniy.ryzhikov.callstatistics.viewmodel.UpdateDBViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateDBFragment : Fragment() {
    private var _binding: FragmentUpdateDbBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpdateDBViewModel by activityViewModels()
    private val scopeLoad = CoroutineScope(Job())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateDbBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadAnimation()
        updateDB()
        viewModel.progressBarLiveData.observe(viewLifecycleOwner) { progress ->
            updateProgressBar(progress)
        }
        viewModel.updateFinishedLideData.observe(viewLifecycleOwner) {
            startHomeFragment()
        }
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

    private fun updateProgressBar(progress: Double) {
        binding.progressBar.setProgressPercentage(progress, false)
    }

    private fun updateDB() {
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.DATE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE
        )
        val where = ""

        val cursor: Cursor = requireActivity().contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            where,
            null,
            null
        ) ?: return
        viewModel.addPhoneTalkByCursor(cursor)
    }

    private fun startHomeFragment() {
        scopeLoad.cancel()
        (requireActivity() as MainActivity).startFragment(HomeFragment())
        onDestroy()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}