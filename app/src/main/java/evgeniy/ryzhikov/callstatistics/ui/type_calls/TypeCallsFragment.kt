package evgeniy.ryzhikov.callstatistics.ui.type_calls

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.databinding.FragmentTypeCallsBinding
import evgeniy.ryzhikov.callstatistics.models.TypeCallsContainer
import evgeniy.ryzhikov.callstatistics.ui.MainActivity.Companion.KEY_TYPE_CALL
import evgeniy.ryzhikov.callstatistics.ui.dialogs.DetailPhoneNumberFragment
import evgeniy.ryzhikov.callstatistics.ui.dialogs.DetailPhoneTalkFragment
import evgeniy.ryzhikov.callstatistics.ui.extensions.getParcelableCompat
import evgeniy.ryzhikov.callstatistics.ui.type_calls.rv.BottomSpacingItemDecoration
import evgeniy.ryzhikov.callstatistics.ui.type_calls.rv.TopCallsAdapter
import evgeniy.ryzhikov.callstatistics.ui.type_calls.rv.TopItem
import evgeniy.ryzhikov.callstatistics.utils.ClickListener
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE
import evgeniy.ryzhikov.callstatistics.utils.convertDuration

open class TypeCallsFragment() : Fragment(R.layout.fragment_type_calls) {
    private var _binding: FragmentTypeCallsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TypeCallsViewModel by viewModels()

    private val adapter = TopCallsAdapter()
    private lateinit var recyclerView: RecyclerView

    private var currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
    private var typeRecyclerViewMap = mapOf<TypeRecyclerView, @receiver:IdRes Int>()

    private var typeCalls: Int = INCOMING_TYPE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTypeCallsBinding.bind(view)

        typeCalls = arguments?.getParcelableCompat<TypeCallsContainer>(KEY_TYPE_CALL)?.typeCalls!!

        initDataMap()
        initActionBar()
        setLiveDataObservers()
        viewModel.getTopPhoneNumberByPhoneTalk(typeCalls)
        initRV()
        lockButton()
        addOnClickListener()
    }


    private fun initDataMap() {
        typeRecyclerViewMap = mapOf<TypeRecyclerView, @receiver:IdRes Int>(
            TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK to when (typeCalls) {
                INCOMING_TYPE -> R.string.header_top_incoming
                else -> R.string.header_top_outgoing
            },
            TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK to when (typeCalls) {
                INCOMING_TYPE -> R.string.header_longest_incoming
                else -> R.string.header_longest_outgoing
            },
            TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING to when (typeCalls) {
                INCOMING_TYPE -> R.string.header_with_longest_incoming
                else -> R.string.header_with_longest_outgoing
            },
        )
    }

    private fun initActionBar() {
        binding.actionBar.apply {
            isAnimate = true
            setContent(
                caption = when (typeCalls) {
                    INCOMING_TYPE -> getString(R.string.incoming)
                    else -> getString(R.string.outgoing)
                },
                topName = when (typeCalls) {
                    INCOMING_TYPE -> getString(R.string.label_count_calls_with_incoming)
//                    INCOMING_TYPE -> "это достаточно длинный текст, что бы моно было "
                    else -> getString(R.string.label_count_calls_with_outgoing)
                },
                bottomName = when (typeCalls) {
                    INCOMING_TYPE -> getString(R.string.label_total_incoming)
                    else -> getString(R.string.label_total_outgoing)
                }
            )
        }
    }


    private fun setLiveDataObservers() {
        with(viewModel) {
            topPhoneNumberByPhoneTalk.observe(viewLifecycleOwner) { phoneNumbers ->
                processTopPhoneNumberByPhoneTalk(phoneNumbers)
            }

            topLongestPhoneTalk.observe(viewLifecycleOwner) { phoneTalks ->
                processTopLongestPhoneTalk(phoneTalks)
            }

            topPhoneNumberWithLongest.observe(viewLifecycleOwner) { phoneNumbers ->
                processTopPhoneNumberWithLongest(phoneNumbers)
            }
        }
    }

    private fun processTopPhoneNumberByPhoneTalk(phoneNumbers: List<PhoneNumber>) {
        displayGeneralInfo()

        val topItems = mutableListOf<TopItem>()
        phoneNumbers.forEach { phoneNumber ->
            topItems.add(
                TopItem(
                    contactName = phoneNumber.contactName,
                    phoneNumber = phoneNumber.phoneNumber,
                    value = when (typeCalls) {
                        INCOMING_TYPE -> phoneNumber.counterIncoming.toString()
                        else -> phoneNumber.counterOutgoing.toString()
                    },
                    phoneData = phoneNumber,
                    listener
                )
            )
        }
        updateRecyclerView(topItems)
    }

    private fun displayGeneralInfo() {
//        with(binding) {
//            tvTotalNumbers.text = viewModel.totalNumber.toString()
//            tvTotalTalk.text = viewModel.totalTalks.toString()
//        }
        binding.actionBar.setContent(
            topValue = viewModel.totalNumber.toString(),
            bottomValue = viewModel.totalTalks.toString()
        )
    }

    private fun processTopLongestPhoneTalk(phoneTalks: List<PhoneTalk>) {
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

    private fun processTopPhoneNumberWithLongest(phoneNumbers: List<PhoneNumber>) {
        val topItems = mutableListOf<TopItem>()
        phoneNumbers.forEach { phoneNumber ->
            topItems.add(
                TopItem(
                    contactName = phoneNumber.contactName,
                    phoneNumber = phoneNumber.phoneNumber,
                    value = convertDuration(
                        when (typeCalls) {
                            INCOMING_TYPE -> phoneNumber.durationIncoming
                            else -> phoneNumber.durationOutgoing
                        }
                    ),
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
                    viewModel.getTopLongestPhoneTalk(typeCalls)
                }

                TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING
                    viewModel.getTopPhoneNumbersWithLongest(typeCalls)
                }

                TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
                    viewModel.getTopPhoneNumberByPhoneTalk(typeCalls)
                }
            }
        }

        binding.back.setOnClickListener {
            lockButton()
            when (currentTypeRecyclerView) {
                TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING
                    viewModel.getTopPhoneNumbersWithLongest(typeCalls)
                }

                TypeRecyclerView.TOP_PHONE_NUMBERS_WITH_LONGEST_INCOMING -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK
                    viewModel.getTopLongestPhoneTalk(typeCalls)
                }

                TypeRecyclerView.TOP_LONGEST_INCOMING_PHONE_TALK -> {
                    currentTypeRecyclerView = TypeRecyclerView.TOP_PHONE_NUMBER_BY_INCOMING_PHONE_TALK
                    viewModel.getTopPhoneNumberByPhoneTalk(typeCalls)
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
        adapter.clear()
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