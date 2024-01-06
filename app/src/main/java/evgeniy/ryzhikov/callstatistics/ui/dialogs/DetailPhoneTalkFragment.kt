package evgeniy.ryzhikov.callstatistics.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.fragment.app.DialogFragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentDetailPhoneTalkBinding
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.utils.convertTypePhoneTalkToString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailPhoneTalkFragment(private val phoneTalk: PhoneTalk) : DialogFragment(R.layout.fragment_detail_phone_talk) {
    private var _binding: FragmentDetailPhoneTalkBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailPhoneTalkBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addOnClickListener()
        displayData()
    }

    private fun displayData() {
        with(binding) {
            contactName.text = phoneTalk.contactName
            phoneNumber.text = phoneTalk.phoneNumber
            type.text = convertTypePhoneTalkToString(phoneTalk.type)
            duration.text = convertDuration(duration = phoneTalk.duration, isSeparated = true)
            date.text = getDateFromISO8601(phoneTalk.dateTime)
            time.text = getTimeFromISO8601(phoneTalk.dateTime)
        }
    }

    private fun getTimeFromISO8601(isoFormat: String): String =
        DateFormat.format("HH:mm:ss", getParsedDate(isoFormat)).toString()

    private fun getDateFromISO8601(isoFormat: String): String =
        DateFormat.format("yyyy-MM-dd", getParsedDate(isoFormat)).toString()
    private fun getParsedDate(isoFormat: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.parse(isoFormat)
    }


    private fun addOnClickListener() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}