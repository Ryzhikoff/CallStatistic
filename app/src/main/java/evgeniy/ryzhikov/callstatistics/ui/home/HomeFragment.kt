package evgeniy.ryzhikov.callstatistics.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentHomeBinding
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneNumbers
import evgeniy.ryzhikov.callstatistics.utils.convertDuration

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        onCreateAnimation()
        viewModel.getGeneralData()
        viewModel.consolidatedPhoneNumbers.observe(viewLifecycleOwner) { consolidatedPhoneNumbers ->
            displayStat(consolidatedPhoneNumbers)
        }
    }

    private fun onCreateAnimation() {
        binding.infoContainer.apply {
            animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fall_from_top)
            animate()
        }
    }


    private fun displayStat(consolidatedPhoneNumbers: ConsolidatedPhoneNumbers) {
        with(binding) {
            tvTotalNumbers.text = consolidatedPhoneNumbers.totalNumbers.toString()
            tvTotalTalk.text = consolidatedPhoneNumbers.totalTalks.toString()
            tvTotalDuration.text = convertDuration(duration = consolidatedPhoneNumbers.duration, isSeparated = true)

            val listOfPair = mutableListOf<Pair<Int, String>>()

            if (consolidatedPhoneNumbers.incoming > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.incoming, resources.getString(R.string.incoming)))
            if (consolidatedPhoneNumbers.outgoing > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.outgoing, resources.getString(R.string.outgoing)))
            if (consolidatedPhoneNumbers.missed > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.missed, resources.getString(R.string.missed)))
            if (consolidatedPhoneNumbers.rejected > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.rejected, resources.getString(R.string.rejected)))
            if (consolidatedPhoneNumbers.blocked > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.blocked, resources.getString(R.string.blocked)))
            if (consolidatedPhoneNumbers.answeredExternally > 0)
                listOfPair.add(Pair(consolidatedPhoneNumbers.answeredExternally, resources.getString(R.string.answered_externally)))

            chartCalls.setDataChart(listOfPair)
            chartCalls.visibility = View.VISIBLE
            chartCalls.startAnimation()

            chartDuration.isConvertDuration = true
            chartDuration.setDataChart(
                listOf(
                    Pair(consolidatedPhoneNumbers.durationInc.toInt(), resources.getString(R.string.incoming)),
                    Pair(consolidatedPhoneNumbers.durationOut.toInt(), resources.getString(R.string.outgoing)),
                )
            )
            chartDuration.visibility = View.VISIBLE
            chartDuration.startAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}