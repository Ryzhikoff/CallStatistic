package evgeniy.ryzhikov.callstatistics.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentDetailPhoneTalkBinding
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.utils.convertTypePhoneTalkToString

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
            date.text = phoneTalk.dateTime
            time.text = phoneTalk.dateTime
        }
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