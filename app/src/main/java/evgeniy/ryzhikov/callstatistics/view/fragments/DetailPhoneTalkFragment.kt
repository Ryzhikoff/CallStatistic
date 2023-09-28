package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentDetailPhoneTalkBinding
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.utils.convertTypePhoneTalkToString
import evgeniy.ryzhikov.callstatistics.utils.getDateFromLong
import evgeniy.ryzhikov.callstatistics.utils.getTimeFromLong

class DetailPhoneTalkFragment(private val phoneTalk: PhoneTalk) : DialogFragment() {
    private var _binding: FragmentDetailPhoneTalkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPhoneTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        displayData()
    }

    private fun displayData() {
        with(binding) {
            contactName.text = phoneTalk.contactName
            phoneNumber.text = phoneTalk.phoneNumber
            type.text = convertTypePhoneTalkToString(phoneTalk.type)
            duration.text = convertDuration(duration = phoneTalk.duration, isSeparated = true)
            date.text = getDateFromLong(phoneTalk.date)
            time.text = getTimeFromLong(phoneTalk.date)
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