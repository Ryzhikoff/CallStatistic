package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentStatByPeriodBinding
import kotlin.random.Random

class StatByPeriodFragment : Fragment() {
    private var _binding: FragmentStatByPeriodBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatByPeriodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val incoming = mutableListOf<BarEntry>()
        val outgoing = mutableListOf<BarEntry>()

        repeat(10) {index ->
            incoming.add(BarEntry(index.toFloat(), Random.nextInt(0, 20).toFloat()))
            outgoing.add(BarEntry(index.toFloat(), Random.nextInt(0, 20).toFloat()))
        }
        val incomingSet = BarDataSet(incoming, "Входящие").apply {
            color = ContextCompat.getColor(requireContext(),R.color.purple_main)
        }
        val outgoingSet = BarDataSet(outgoing, "Исходящие").apply {
            color = ContextCompat.getColor(requireContext(),R.color.blue_main)
        }

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset

        val barData = BarData(incomingSet, outgoingSet)
        barData.barWidth = barWidth
        binding.barChartCalls.apply {
            data = barData
            groupBars(0f, groupSpace, barSpace)
            description.text = ""
            setNoDataText("На сегодня ни чего нет")
            setDrawBorders(false)
            legend.textColor = ContextCompat.getColor(requireContext(),R.color.text)
            animateY(1000)
        }

    }

    private fun getFloatArray(): FloatArray {
        val array = FloatArray(size = 10)
        repeat(10) {i ->
            array[i] = Random.nextInt(0, 20).toFloat()
        }
        return array
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}