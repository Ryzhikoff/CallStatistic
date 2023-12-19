package evgeniy.ryzhikov.callstatistics.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.databinding.FragmentDetailPhoneNumberBinding
import evgeniy.ryzhikov.callstatistics.utils.convertDuration

class DetailPhoneNumberFragment(val phoneNumber: PhoneNumber) : DialogFragment(R.layout.fragment_detail_phone_number) {
    private var _binding: FragmentDetailPhoneNumberBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailPhoneNumberBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        displayMainDataPhoneNumber()
        displayPhoneTalkChart()
        displayDurationChart()
        addOnClickListener()
    }

    private fun displayMainDataPhoneNumber() {
        with(binding) {
            name.text = phoneNumber.contactName
            number.text = phoneNumber.phoneNumber
        }
    }

    private fun displayPhoneTalkChart() {
        val listOfPair = mutableListOf<Pair<Int, String>>()

        if (phoneNumber.counterIncoming > 0)
            listOfPair.add(Pair(phoneNumber.counterIncoming, resources.getString(R.string.incoming)))
        if (phoneNumber.counterOutgoing > 0)
            listOfPair.add(Pair(phoneNumber.counterOutgoing, resources.getString(R.string.outgoing)))
        if (phoneNumber.counterMissed > 0)
            listOfPair.add(Pair(phoneNumber.counterMissed, resources.getString(R.string.missed)))
        if (phoneNumber.counterRejected > 0)
            listOfPair.add(Pair(phoneNumber.counterRejected, resources.getString(R.string.rejected)))
        if (phoneNumber.counterBlocked > 0)
            listOfPair.add(Pair(phoneNumber.counterBlocked, resources.getString(R.string.blocked)))
        if (phoneNumber.counterVoicemail > 0)
            listOfPair.add(Pair(phoneNumber.counterVoicemail, resources.getString(R.string.voice_email)))
        if (phoneNumber.counterAnsweredExternally > 0)
            listOfPair.add(Pair(phoneNumber.counterAnsweredExternally, resources.getString(R.string.answered_externally)))

        with(binding) {
            totalTalk.text = phoneNumber.counterTotal.toString()
            chartCalls.setDataChart(listOfPair)
            chartCalls.visibility = View.VISIBLE
            chartCalls.startAnimation()
        }
    }

    private fun displayDurationChart() {
        val listOfPair = mutableListOf<Pair<Int, String>>()

        if (phoneNumber.durationIncoming > 0)
            listOfPair.add(Pair(phoneNumber.durationIncoming.toInt(), resources.getString(R.string.incoming)))
        if (phoneNumber.durationOutgoing > 0)
            listOfPair.add(Pair(phoneNumber.durationOutgoing.toInt(), resources.getString(R.string.outgoing)))

        with(binding) {
            duration.text = convertDuration(phoneNumber.durationIncoming + phoneNumber.durationOutgoing, isSeparated = true)
            chartDuration.isConvertDuration = true
            chartDuration.setDataChart(listOfPair)
            chartDuration.visibility = View.VISIBLE
            chartDuration.startAnimation()
        }
    }

    private fun addOnClickListener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}