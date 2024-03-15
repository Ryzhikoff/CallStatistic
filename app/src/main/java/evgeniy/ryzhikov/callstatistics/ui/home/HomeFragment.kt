package evgeniy.ryzhikov.callstatistics.ui.home

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentHomeBinding
import evgeniy.ryzhikov.callstatistics.utils.ANIMATION_DURATION_TO_GRAPHS
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneNumbers
import evgeniy.ryzhikov.callstatistics.utils.convertDuration

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        initActionBar()
        viewModel.getGeneralData()
        viewModel.consolidatedPhoneNumbers.observe(viewLifecycleOwner) { consolidatedPhoneNumbers ->
            displayStat(consolidatedPhoneNumbers)
        }

        initFabMenu()
    }

    private fun initFabMenu() = with(binding) {
        fabMenu.apply {
            animation = AnimationUtils.loadAnimation(context, R.anim.fall_from_top)
            animate()
        }
        backup.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_backupFragment)
        }

    }

    private fun initActionBar() {
        binding.actionBar.apply {
            isAnimate = true
            setContent(
                caption = getString(R.string.home_title),
                topName = getString(R.string.label_total_number),
                bottomName = getString(R.string.label_total_calls),
            )
        }
    }

    private fun displayStat(consolidatedPhoneNumbers: ConsolidatedPhoneNumbers) {
        with(binding) {
            actionBar.setContent(
                topValue = consolidatedPhoneNumbers.totalNumbers.toString(),
                bottomValue = consolidatedPhoneNumbers.totalTalks.toString()
            )
            tvTotalDuration.text = convertDuration(duration = consolidatedPhoneNumbers.duration, isSeparated = true)
            tvAverageDuration.text = convertDuration(duration = consolidatedPhoneNumbers.totalAverageDuration, isSeparated = true)

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
            chartsVisible(true)
            chartCalls.startAnimation()

            chartDuration.isConvertDuration = true
            chartDuration.isDrawTextInCircle = false
            chartDuration.setDataChart(
                listOf(
                    Pair(consolidatedPhoneNumbers.durationInc.toInt(), resources.getString(R.string.incoming)),
                    Pair(consolidatedPhoneNumbers.durationOut.toInt(), resources.getString(R.string.outgoing)),
                )
            )
            chartDuration.startAnimation()

            if (consolidatedPhoneNumbers.incomingAverageDuration > 0L || consolidatedPhoneNumbers.outgoingAverageDuration > 0) {
                incomingAverage.setAnimationLength(ANIMATION_DURATION_TO_GRAPHS)
                outgoingAverage.setAnimationLength(ANIMATION_DURATION_TO_GRAPHS)

                if (consolidatedPhoneNumbers.incomingAverageDuration > consolidatedPhoneNumbers.outgoingAverageDuration) {
                    incomingAverage.setProgressPercentage(100.0, true)

                    if (consolidatedPhoneNumbers.incomingAverageDuration == 0L) {
                        outgoingAverage.setProgressPercentage(0.0, true)
                    } else {
                        val outgoing =
                            consolidatedPhoneNumbers.outgoingAverageDuration.toDouble() / consolidatedPhoneNumbers.incomingAverageDuration * 100.0
                        outgoingAverage.setProgressPercentage(outgoing, true)
                    }
                } else {
                    outgoingAverage.setProgressPercentage(100.0, true)

                    if (consolidatedPhoneNumbers.outgoingAverageDuration == 0L) {
                        incomingAverage.setProgressPercentage(0.0, true)
                    } else {
                        val outgoing =
                            consolidatedPhoneNumbers.incomingAverageDuration.toDouble() / consolidatedPhoneNumbers.outgoingAverageDuration * 100.0
                        incomingAverage.setProgressPercentage(outgoing, true)
                    }
                }
            }

            val incoming =
                "${getString(R.string.incoming)} ${convertDuration(consolidatedPhoneNumbers.incomingAverageDuration, isSeparated = true)}"
            val outgoing =
                "${getString(R.string.outgoing)} ${convertDuration(consolidatedPhoneNumbers.outgoingAverageDuration, isSeparated = true)}"
            labelAverangeIncoming.text = incoming
            labelAverangeOutgoing.text = outgoing
        }
    }

    private fun chartsVisible(value: Boolean) = with(binding) {
        chartCalls.isVisible = value
        chartDuration.isVisible = value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}