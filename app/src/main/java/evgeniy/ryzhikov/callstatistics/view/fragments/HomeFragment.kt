package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentHomeBinding
import evgeniy.ryzhikov.callstatistics.utils.GeneralData
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.viewmodel.HomeFragmentViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLoadAnimation()
        viewModel.getGeneralData()
        viewModel.generalDataLiveData.observe(viewLifecycleOwner) { generalData ->
            displayStat(generalData)
        }
    }

    private fun startLoadAnimation() {

    }

    private fun stopLoadAnimation() {

    }

    private fun displayStat(generalData: GeneralData) {
        with(binding) {
            tvTotalNumbers.text = generalData.totalNumbers.toString()
            tvTotalTalk.text = generalData.totalTalks.toString()
            tvTotalDuration.text = convertDuration(duration = generalData.duration, isSeparated = true)

            val listOfPair = mutableListOf<Pair<Int, String>>()

            if (generalData.incoming > 0)
                listOfPair.add(Pair(generalData.incoming, resources.getString(R.string.incoming)))
            if (generalData.outgoing > 0)
                listOfPair.add(Pair(generalData.outgoing, resources.getString(R.string.outgoing)))
            if (generalData.missed > 0)
                listOfPair.add(Pair(generalData.missed, resources.getString(R.string.missed)))
            if (generalData.rejected > 0)
                listOfPair.add(Pair(generalData.rejected, resources.getString(R.string.rejected)))
            if (generalData.blocked > 0)
                listOfPair.add(Pair(generalData.blocked, resources.getString(R.string.blocked)))
            if (generalData.answeredExternally > 0)
                listOfPair.add(Pair(generalData.answeredExternally, resources.getString(R.string.answered_externally)))

            chartCalls.setDataChart(listOfPair)
            chartCalls.visibility = View.VISIBLE
            chartCalls.startAnimation()

            chartDuration.isConvertDuration = true
            chartDuration.setDataChart(
                listOf(
                    Pair(generalData.durationInc.toInt(), resources.getString(R.string.incoming)),
                    Pair(generalData.durationOut.toInt(), resources.getString(R.string.outgoing)),
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