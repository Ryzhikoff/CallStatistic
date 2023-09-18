package evgeniy.ryzhikov.callstatistics.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.databinding.FragmentIncomingBinding
import evgeniy.ryzhikov.callstatistics.view.rv.TopCalls
import evgeniy.ryzhikov.callstatistics.view.rv.TopCallsAdapter
import evgeniy.ryzhikov.callstatistics.view.viewmodel.IncomingFragmentViewModel

class IncomingFragment : Fragment() {
    private var _binding: FragmentIncomingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IncomingFragmentViewModel by activityViewModels()

    private val adapter = TopCallsAdapter()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.phoneListLiveData.observe(viewLifecycleOwner) { phoneNumbers ->
            displayData(phoneNumbers)
        }
        viewModel.getPhoneList()
        initRV()
    }


    private fun displayData(phoneNumbers: List<PhoneNumber>) {
        with(binding) {
            tvTotalNumbers.text = viewModel.totalNumber.toString()
            tvTotalTalk.text = viewModel.totalTalks.toString()
        }

        adapter.clear()
        repeat(10) { index ->
            adapter.addItem(
                TopCalls(
                    contactName = phoneNumbers[index].contactName,
                    phoneNumber = phoneNumbers[index].phoneNumber,
                    countCalls = phoneNumbers[index].counterIncoming
                )
            )
        }
        adapter.update()
    }

    private fun initRV() {
        recyclerView = binding.rvMostCalling
        recyclerView.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}