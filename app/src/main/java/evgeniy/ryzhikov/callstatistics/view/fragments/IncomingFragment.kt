package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentIncomingBinding
import evgeniy.ryzhikov.callstatistics.utils.ClickListener
import evgeniy.ryzhikov.callstatistics.utils.convertDuration
import evgeniy.ryzhikov.callstatistics.view.rv.Header
import evgeniy.ryzhikov.callstatistics.view.rv.TopItem
import evgeniy.ryzhikov.callstatistics.view.rv.TopCallsAdapter
import evgeniy.ryzhikov.callstatistics.viewmodel.IncomingFragmentViewModel

class IncomingFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLiveDataObservers()
        viewModel.getTopPhoneNumberByIncomingPhoneTalk()
        initRV()
        lockButton()
        addOnClickListener()
    }

    private fun setLiveDataObservers() {
        with(viewModel) {
            topPhoneNumberByIncomingPhoneTalk.observe(viewLifecycleOwner) { phoneNumbers ->
                displayGeneralInfo()
                displayTopCalling(phoneNumbers)
            }

            topLongestIncomingPhoneTalk.observe(viewLifecycleOwner) { phoneTalks ->
                displayLongestIncomingCalls(phoneTalks)
            }

            topPhoneNumberWithLongestIncoming.observe(viewLifecycleOwner) { phoneNumbers ->
                val topItems = mutableListOf<TopItem>()
                phoneNumbers.forEach { phoneNumber ->
                    topItems.add(TopItem(
                        contactName = phoneNumber.contactName,
                        phoneNumber = phoneNumber.phoneNumber,
                        value = convertDuration(phoneNumber.durationIncoming),
                        phoneData = phoneNumber,
                        listener
                    ))
                }
                updateRecyclerView(topItems)
            }
        }
    }

    private fun displayGeneralInfo() {
        with(binding) {
            tvTotalNumbers.text = viewModel.totalNumber.toString()
            tvTotalTalk.text = viewModel.totalTalks.toString()
        }
    }

    private fun displayTopCalling(phoneNumbers: List<PhoneNumber>) {
        adapter.clear()
        adapter.addItem(Header(resources.getString(R.string.header_top_incoming)))
        repeat(10) { index ->
            adapter.addItem(
                TopItem(
                    contactName = phoneNumbers[index].contactName,
                    phoneNumber = phoneNumbers[index].phoneNumber,
                    value = phoneNumbers[index].counterIncoming.toString(),
                    phoneData = phoneNumbers[index],
                    listener
                )
            )
        }
        adapter.update()
        unLockButton()
    }

    private fun displayLongestIncomingCalls(phoneTalks: List<PhoneTalk>) {
        adapter.clear()
        adapter.addItem(Header(resources.getString(R.string.header_longest_incoming)))
        repeat(10) { index ->
            adapter.addItem(
                TopItem(
                    contactName = phoneTalks[index].contactName,
                    phoneNumber = phoneTalks[index].phoneNumber,
                    value = convertDuration(duration = phoneTalks[index].duration),
                    phoneData = phoneTalks[index],
                    listener
                )
            )
        }

        unLockButton()
    }

    private fun updateRecyclerView(topItems: List<TopItem>) {
        adapter.clear()
        adapter.addItem(Header(resources.getString(typeRecyclerViewMap[currentTypeRecyclerView]!!)))

        topItems.forEach {topItem ->
            adapter.addItem(topItem)
        }
        unLockButton()
    }

    private val TAG_DETAIL_PHONE_TALK_FRAGMENT = "detail_phone_talk_fragment"
    private val TAG_DETAIL_PHONE_NUMBER_FRAGMENT = "detail_phone_number_fragment"
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

    }

    /**  forward:
        TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK ->
        TOP_LONGEST_INCOMING_PHONE_TALK ->
        TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING ->
     */
    private fun addOnClickListener() {
        binding.forward.setOnClickListener {
            lockButton()
            when(currentTypeRecyclerView) {
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
            when(currentTypeRecyclerView) {
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
}