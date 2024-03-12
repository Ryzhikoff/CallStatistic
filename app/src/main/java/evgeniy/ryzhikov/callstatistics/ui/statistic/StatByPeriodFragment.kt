package evgeniy.ryzhikov.callstatistics.ui.statistic

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.databinding.FragmentStatByPeriodBinding
import evgeniy.ryzhikov.callstatistics.utils.ANIMATION_DURATION_TO_GRAPHS
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneTalks
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import java.util.Calendar

class StatByPeriodFragment : Fragment(R.layout.fragment_stat_by_period) {
    private var _binding: FragmentStatByPeriodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatByPeriodViewModel by activityViewModels()
    private var selectedDay: String = ""
    private val buttons: MutableList<View> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatByPeriodBinding.bind(view)

        initActionBar()
        setOnClickListeners()
        addButtons()

        viewModel.consolidatedPhoneTalksLiveData.observe(viewLifecycleOwner) {
            displayStatByDay(it)
        }

        selectedDay = getFormattedDay(System.currentTimeMillis())
        viewModel.getPhoneTalksByDay(selectedDay)
        initFabMenu()
    }

    private fun initFabMenu() = with(binding) {
        fabMenu.apply {
            animation = AnimationUtils.loadAnimation(context, R.anim.fall_from_top)
            animate()
        }
        backup.setOnClickListener {
            findNavController().navigate(R.id.action_statByPeriodFragment_to_backupFragment2)
        }

    }

    private fun initActionBar() {
        binding.actionBar.apply {
            isAnimate = true
            setContent(
                caption = getString(R.string.menu_total_statistic),
                subhead = getString(R.string.label_stat_by))
        }
    }

    private fun addButtons() {
        binding.apply {
            buttons.add(chooseDay)
            buttons.add(today)
            buttons.add(yesterday)
        }
    }

    private fun setOnClickListeners() {
        with(binding) {
            today.setOnClickListener {
                selectButton(it)
                selectedDay = getFormattedDay(System.currentTimeMillis())
                viewModel.getPhoneTalksByDay(selectedDay)
            }

            yesterday.setOnClickListener {
                selectButton(it)
                selectedDay = getFormattedDay(System.currentTimeMillis(), -1)
                viewModel.getPhoneTalksByDay(selectedDay)
            }

            chooseDay.setOnClickListener {
                selectButton(it)
                chooseDay()
            }
        }
    }

    private fun selectButton(view: View) {
        buttons.forEach {
            it.isSelected = false
        }
        view.isSelected = true
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
//            labelByDay.text = label
//            labelByDay.requestLayout()
            binding.actionBar.setContent(subhead = label)

            chartByDayCount.setDataChart(pairOfCount)
            chartByDayCount.requestLayout()
            chartByDayCount.startAnimation()

            chartByDayDuration.isConvertDuration = true
            chartByDayDuration.setDataChart(pairOfDuration)
            chartByDayDuration.requestLayout()
            chartByDayDuration.startAnimation()

            tvAverageDuration.text = convertDuration(duration = consolidatedPhoneTalks.totalAverageDuration, isSeparated = true)

            if (consolidatedPhoneTalks.incomingAverageDuration > 0L || consolidatedPhoneTalks.outgoingAverageDuration > 0) {
                incomingAverage.setAnimationLength(ANIMATION_DURATION_TO_GRAPHS)
                outgoingAverage.setAnimationLength(ANIMATION_DURATION_TO_GRAPHS)

                if (consolidatedPhoneTalks.incomingAverageDuration > consolidatedPhoneTalks.outgoingAverageDuration) {
                    incomingAverage.setProgressPercentage(100.0, true)

                    if (consolidatedPhoneTalks.incomingAverageDuration == 0L) {
                        outgoingAverage.setProgressPercentage(0.0, true)
                    } else {
                        val outgoing =
                            consolidatedPhoneTalks.outgoingAverageDuration.toDouble() / consolidatedPhoneTalks.incomingAverageDuration * 100.0
                        outgoingAverage.setProgressPercentage(outgoing, true)
                    }
                } else {
                    outgoingAverage.setProgressPercentage(100.0, true)

                    if (consolidatedPhoneTalks.outgoingAverageDuration == 0L) {
                        incomingAverage.setProgressPercentage(0.0, true)
                    } else {
                        val outgoing =
                            consolidatedPhoneTalks.incomingAverageDuration.toDouble() / consolidatedPhoneTalks.outgoingAverageDuration * 100.0
                        incomingAverage.setProgressPercentage(outgoing, true)
                    }
                }
            }

            val incoming =
                "${getString(R.string.incoming)} ${convertDuration(consolidatedPhoneTalks.incomingAverageDuration, isSeparated = true)}"
            val outgoing =
                "${getString(R.string.outgoing)} ${convertDuration(consolidatedPhoneTalks.outgoingAverageDuration, isSeparated = true)}"
            labelAverangeIncoming.text = incoming
            labelAverangeOutgoing.text = outgoing
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