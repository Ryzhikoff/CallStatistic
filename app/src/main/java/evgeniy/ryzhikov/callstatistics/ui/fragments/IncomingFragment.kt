package evgeniy.ryzhikov.callstatistics.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentIncomingBinding
import evgeniy.ryzhikov.callstatistics.ui.rv.BottomSpacingItemDecoration
import evgeniy.ryzhikov.callstatistics.utils.ClickListener
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.ui.rv.TopItem
import evgeniy.ryzhikov.callstatistics.ui.rv.TopCallsAdapter
import evgeniy.ryzhikov.callstatistics.viewmodel.IncomingFragmentViewModel

class IncomingFragment : Fragment(R.layout.fragment_incoming) {
    private var _binding: FragmentIncomingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IncomingFragmentViewModel by activityViewModels()

    private val adapter = TopCallsAdapter()
    private lateinit var recyclerView: RecyclerView

    private var currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
    private val typeRecyclerViewMap = mapOf<TypeRecyclerView, @receiver:IdRes Int>(
        TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK to R.string.header_top_incoming,
        TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK to R.string.header_longest_incoming,
        TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING to R.string.header_with_longest_incoming
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIncomingBinding.bind(view)

        onCreateAnimation()
        setLiveDataObservers()
        viewModel.getTopPhoneNumberByIncomingPhoneTalk()
        initRV()
        lockButton()
        addOnClickListener()
    }

    private fun onCreateAnimation() {
        binding.infoContainer.apply {
            animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fall_from_top)
            animate()
        }
    }

    private fun setLiveDataObservers() {
        with(viewModel) {
            topPhoneNumberByIncomingPhoneTalk.observe(viewLifecycleOwner) { phoneNumbers ->
                processTopPhoneNumberByIncomingPhoneTalk(phoneNumbers)
            }

            topLongestIncomingPhoneTalk.observe(viewLifecycleOwner) { phoneTalks ->
                processTopLongestIncomingPhoneTalk(phoneTalks)
            }

            topPhoneNumberWithLongestIncoming.observe(viewLifecycleOwner) { phoneNumbers ->
                processTopPhoneNumberWithLongestIncoming(phoneNumbers)
            }
        }
    }

    private fun processTopPhoneNumberByIncomingPhoneTalk(phoneNumbers: List<PhoneNumber>) {
        displayGeneralInfo()

        val topItems = mutableListOf<TopItem>()
        phoneNumbers.forEach { phoneNumber ->
            topItems.add(
                TopItem(
                    contactName = phoneNumber.contactName,
                    phoneNumber = phoneNumber.phoneNumber,
                    value = phoneNumber.counterIncoming.toString(),
                    phoneData = phoneNumber,
                    listener
                )
            )
        }
        updateRecyclerView(topItems)
    }

    private fun displayGeneralInfo() {
        with(binding) {
            tvTotalNumbers.text = viewModel.totalNumber.toString()
            tvTotalTalk.text = viewModel.totalTalks.toString()
        }
    }

    private fun processTopLongestIncomingPhoneTalk(phoneTalks: List<PhoneTalk>) {
        val topItems = mutableListOf<TopItem>()
        phoneTalks.forEach { phoneTalk ->
            topItems.add(
                TopItem(
                    contactName = phoneTalk.contactName,
                    phoneNumber = phoneTalk.phoneNumber,
                    value = convertDuration(duration = phoneTalk.duration),
                    phoneData = phoneTalk,
                    listener
                )
            )
        }
        updateRecyclerView(topItems)
    }

    private fun processTopPhoneNumberWithLongestIncoming(phoneNumbers: List<PhoneNumber>) {
        val topItems = mutableListOf<TopItem>()
        phoneNumbers.forEach { phoneNumber ->
            topItems.add(
                TopItem(
                    contactName = phoneNumber.contactName,
                    phoneNumber = phoneNumber.phoneNumber,
                    value = convertDuration(phoneNumber.durationIncoming),
                    phoneData = phoneNumber,
                    listener
                )
            )
        }
        updateRecyclerView(topItems)
    }

    private fun updateRecyclerView(topItems: List<TopItem>) {
        binding.rvLabel.text = resources.getString(typeRecyclerViewMap[currentTypeRecyclerView]!!)
        adapter.clear()

        topItems.forEach { topItem ->
            adapter.addItem(topItem)
        }
        recyclerView.scheduleLayoutAnimation()
        unLockButton()
    }

    private val listener = object : ClickListener {
        override fun onClick(phoneData: PhoneData) {
            when (phoneData) {
                is PhoneTalk -> DetailPhoneTalkFragment(phoneData).show(childFragmentManager, TAG_DETAIL_PHONE_TALK_FRAGMENT)
                is PhoneNumber -> DetailPhoneNumberFragment(phoneData).show(childFragmentManager, TAG_DETAIL_PHONE_NUMBER_FRAGMENT)
            }
        }
    }

    private fun initRV() {
        recyclerView = binding.rvMostCalling
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(BottomSpacingItemDecoration(12))

    }

    /**  forward:
    TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK ->
    TOP_LONGEST_INCOMING_PHONE_TALK ->
    TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING ->
     */
    private fun addOnClickListener() {
        binding.forward.setOnClickListener {
            lockButton()
            when (currentTypeRecyclerView) {
                TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK
                    viewModel.getTopLongestIncomingPhoneTalk()
                }

                TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING
                    viewModel.getTopPhoneNumbersWithLongestIncoming()
                }

                TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
                    viewModel.getTopPhoneNumberByIncomingPhoneTalk()
                }
            }
        }

        binding.back.setOnClickListener {
            lockButton()
            when (currentTypeRecyclerView) {
                TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING
                    viewModel.getTopPhoneNumbersWithLongestIncoming()
                }

                TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK
                    viewModel.getTopLongestIncomingPhoneTalk()
                }

                TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
                    viewModel.getTopPhoneNumberByIncomingPhoneTalk()
                }
            }
        }
    }

    private fun lockButton() {
        binding.back.isClickable = false
        binding.forward.isClickable = false
    }

    private fun unLockButton() {
        binding.back.isClickable = true
        binding.forward.isClickable = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private enum class TypeRecyclerView {
        TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK,
        TOP_LONGEST_INCOMING_PHONE_TALK,
        TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING
    }

    private companion object {
        const val TAG_DETAIL_PHONE_TALK_FRAGMENT = "detail_phone_talk_fragment"
        const val TAG_DETAIL_PHONE_NUMBER_FRAGMENT = "detail_phone_number_fragment"
    }
}