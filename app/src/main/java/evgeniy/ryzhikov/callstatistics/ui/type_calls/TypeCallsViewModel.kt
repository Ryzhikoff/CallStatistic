package evgeniy.ryzhikov.callstatistics.ui.type_calls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.utils.COUNT_ELEMENTS_IN_RV
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE
import evgeniy.ryzhikov.callstatistics.utils.TypeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TypeCallsViewModel : ViewModel() {

    val topPhoneNumberByPhoneTalk: LiveData<List<PhoneNumber>> get() = _topPhoneNumberByPhoneTalk
    private val _topPhoneNumberByPhoneTalk = MutableLiveData<List<PhoneNumber>>()

    val topLongestPhoneTalk: LiveData<List<PhoneTalk>> get() = _topLongestPhoneTalk
    private val _topLongestPhoneTalk = MutableLiveData<List<PhoneTalk>>()

    val topPhoneNumberWithLongest: LiveData<List<PhoneNumber>> get() = _topPhoneNumberWithLongest
    private val _topPhoneNumberWithLongest = MutableLiveData<List<PhoneNumber>>()

    var totalNumber = 0
        private set
    var totalTalks = 0
        private set

    @Inject
    lateinit var mainRepository: MainRepository

    init {
        App.instance.appComponent.inject(this)
    }

    fun getTopLongestPhoneTalk(@TypeCall typeCall: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val phoneTalks = mainRepository.getPhoneTalksByType(typeCall)
            _topLongestPhoneTalk.postValue(phoneTalks.take(COUNT_ELEMENTS_IN_RV))
        }
    }

    fun getTopPhoneNumberByPhoneTalk(@TypeCall typeCall: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            totalTalks = 0
            val phoneNumbers = getPhoneNumbers()

            phoneNumbers.forEach { phoneNumber ->
                totalTalks += when (typeCall) {
                    INCOMING_TYPE -> phoneNumber.counterIncoming
                    else -> phoneNumber.counterOutgoing
                }
            }

            val sortedPhoneNumbers = phoneNumbers.sortedByDescending { phoneNumber ->
                when (typeCall) {
                    INCOMING_TYPE -> phoneNumber.counterIncoming
                    else -> phoneNumber.counterOutgoing
                }
            }

            totalNumber = phoneNumbers.size

            _topPhoneNumberByPhoneTalk.postValue(sortedPhoneNumbers.take(COUNT_ELEMENTS_IN_RV))
        }
    }

    fun getTopPhoneNumbersWithLongest(@TypeCall typeCall: Int) {
        viewModelScope.launch (Dispatchers.IO){
            val phoneNumbers = getPhoneNumbers()

            val sortedPhoneNumbers = phoneNumbers.sortedByDescending { phoneNumber ->
                when (typeCall) {
                    INCOMING_TYPE -> phoneNumber.durationIncoming
                    else -> phoneNumber.durationOutgoing
                }
            }
            _topPhoneNumberWithLongest.postValue(sortedPhoneNumbers.take(COUNT_ELEMENTS_IN_RV))
        }

    }

    private suspend fun getPhoneNumbers() : List<PhoneNumber> {
        return suspendCoroutine { continuation ->
            val phoneNumbers = mainRepository.getAllPhoneNumbers()
            continuation.resume(phoneNumbers)
        }
    }

}