package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import evgeniy.ryzhikov.callstatistics.databinding.FragmentHomeBinding
import evgeniy.ryzhikov.callstatistics.utils.GeneralData
import evgeniy.ryzhikov.callstatistics.view.viewmodel.HomeFragmentViewModel

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
            tvTotalDuration.text = generalData.duration.toString()

            chartCalls.setDataChart(
                listOf(
                    Pair(generalData.incoming, "Входящие"),
                    Pair(generalData.outgoing, "Исходящие"),
                    Pair(generalData.missed, "Пропущенные"),
                    Pair(generalData.rejected, "Отклоненные"),
                    Pair(generalData.blocked, "Заблокированные"),
                    Pair(generalData.answeredExternally, "Принятые на другом устройстве"),
                )
            )
            chartCalls.visibility = View.VISIBLE
            chartCalls.startAnimation()

            chartDuration.setDataChart(
                listOf(
                    Pair(generalData.outgoing, "Исходящие"),
                    Pair(generalData.incoming, "Входящие")
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