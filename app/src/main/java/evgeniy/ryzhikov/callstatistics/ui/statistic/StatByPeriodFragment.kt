package evgeniy.ryzhikov.callstatistics.ui.statistic

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentStatByPeriodBinding
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneTalks
import java.util.Calendar

class StatByPeriodFragment : Fragment(R.layout.fragment_stat_by_period) {
    private var _binding: FragmentStatByPeriodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatByPeriodViewModel by activityViewModels()
    private var selectedDay: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatByPeriodBinding.bind(view)

        onCreateAnimation()
        setOnClickListeners()

        viewModel.consolidatedPhoneTalksLiveData.observe(viewLifecycleOwner) {
            displayStatByDay(it)
        }

        selectedDay = getFormattedDay(System.currentTimeMillis())
        viewModel.getPhoneTalksByDay(selectedDay)
    }

    private fun onCreateAnimation() {
        binding.infoContainer.apply {
            animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fall_from_top)
            animate()
        }
    }

    private fun setOnClickListeners() {
        with(binding) {
            today.setOnClickListener {
                selectedDay = getFormattedDay(System.currentTimeMillis())
                viewModel.getPhoneTalksByDay(selectedDay)
            }

            yesterday.setOnClickListener {
                selectedDay = getFormattedDay(System.currentTimeMillis(), -1)
                viewModel.getPhoneTalksByDay(selectedDay)
            }

            chooseDay.setOnClickListener {
                chooseDay()
            }
        }
    }

    private fun getFormattedDay(millis: Long, offsetDay: Int = 0): String {
        val calendar = Calendar.Builder()
            .setInstant(millis)
            .build().apply {
                this.add(Calendar.DAY_OF_MONTH, offsetDay)
            }
        return DateFormat.format("yyyy-MM-dd", calendar.time).toString()
    }

    private fun chooseDay() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.label_choose_day))
            .setSelection(System.currentTimeMillis())
            .build()
        datePicker.show(childFragmentManager, TAG_DATE_PICKER)
        datePicker.addOnPositiveButtonClickListener { millis ->
            selectedDay = getFormattedDay(millis)
            viewModel.getPhoneTalksByDay(selectedDay)
        }
    }

    private fun displayStatByDay(consolidatedPhoneTalks: ConsolidatedPhoneTalks) {
        val pairOfCount = listOf(
            Pair(consolidatedPhoneTalks.countIncoming, resources.getString(R.string.incoming)),
            Pair(consolidatedPhoneTalks.countOutgoing, resources.getString(R.string.outgoing))
        )

        val pairOfDuration = listOf(
            Pair(consolidatedPhoneTalks.durationIncoming.toInt(), resources.getString(R.string.incoming)),
            Pair(consolidatedPhoneTalks.durationOuting.toInt(), resources.getString(R.string.outgoing))
        )

        with(binding) {

            val label = resources.getString(R.string.label_stat_by) + selectedDay
            labelByDay.text = label
            labelByDay.requestLayout()

            chartByDayCount.setDataChart(pairOfCount)
            chartByDayCount.requestLayout()
            chartByDayCount.startAnimation()

            chartByDayDuration.isConvertDuration = true
            chartByDayDuration.setDataChart(pairOfDuration)
            chartByDayDuration.requestLayout()
            chartByDayDuration.startAnimation()
        }

    }

//    private fun createChart(consolidatedPhoneTalks: ConsolidatedPhoneTalks) {
//
//        println("day: ${consolidatedPhoneTalks.dateTime} count: ${consolidatedPhoneTalks.count} duration: ${consolidatedPhoneTalks.duration}")
//
//        val incoming = mutableListOf<BarEntry>()
//        val outgoing = mutableListOf<BarEntry>()
//
//        repeat(10) { index ->
//            incoming.add(BarEntry(index.toFloat(), Random.nextInt(0, 20).toFloat()))
//            outgoing.add(BarEntry(index.toFloat(), Random.nextInt(0, 20).toFloat()))
//        }
//        val incomingSet = BarDataSet(incoming, "Входящие").apply {
//            color = ContextCompat.getColor(requireContext(), R.color.purple_main)
//        }
//        val outgoingSet = BarDataSet(outgoing, "Исходящие").apply {
//            color = ContextCompat.getColor(requireContext(), R.color.blue_main)
//        }
//
//        val groupSpace = 0.06f
//        val barSpace = 0.02f // x2 dataset
//        val barWidth = 0.45f // x2 dataset
//
//        val barData = BarData(incomingSet, outgoingSet)
//        barData.barWidth = barWidth
//        binding.barChartCalls.apply {
//            data = barData
//            groupBars(0f, groupSpace, barSpace)
//            description.text = ""
//            setNoDataText("На сегодня ни чего нет")
//            setDrawBorders(false)
//            legend.textColor = ContextCompat.getColor(requireContext(), R.color.text)
//            animateY(1000)
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG_DATE_PICKER = "DATE_PICKER"
    }
}