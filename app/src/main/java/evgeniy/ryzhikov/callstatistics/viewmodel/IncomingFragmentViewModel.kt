package evgeniy.ryzhikov.callstatistics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.utils.COUNT_ELEMENTS_IN_RV
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IncomingFragmentViewModel : ViewModel() {

    val topPhoneNumberByIncomingPhoneTalk = MutableLiveData<List<PhoneNumber>>()
    val topLongestIncomingPhoneTalk = MutableLiveData<List<PhoneTalk>>()
    val topPhoneNumberWithLongestIncoming = MutableLiveData<List<PhoneNumber>>()

    var totalNumber = 0
        private set
    var totalTalks = 0
        private set

    @Inject
    lateinit var mainRepository: MainRepository

    init {
        App.instance.appComponent.inject(this)
    }

    fun getTopLongestIncomingPhoneTalk() {
        viewModelScope.launch(Dispatchers.IO) {
            val phoneTalks = mainRepository.getAllPhoneTalk()

            val filtersPhoneTalks = phoneTalks.filter { phoneTalk ->
                phoneTalk.type == INCOMING_TYPE
            }

            val sortedPhoneTalks = filtersPhoneTalks.sortedByDescending { phoneTalk ->
                phoneTalk.duration
            }

            topLongestIncomingPhoneTalk.postValue(sortedPhoneTalks)
        }
    }

    fun getTopPhoneNumberByIncomingPhoneTalk() {
        viewModelScope.launch(Dispatchers.IO) {
            totalTalks = 0
            val phoneNumbers = getPhoneNumbersWithIncoming()

            phoneNumbers.forEach { phoneNumber ->
                totalTalks += phoneNumber.counterIncoming
            }

            val sortedPhoneNumbers = phoneNumbers.sortedByDescending { phoneNumber ->
                phoneNumber.counterIncoming
            }

            totalNumber = phoneNumbers.size

            topPhoneNumberByIncomingPhoneTalk.postValue(sortedPhoneNumbers)
        }
    }

    fun getTopPhoneNumbersWithLongestIncoming() {
        viewModelScope.launch (Dispatchers.IO){
            val phoneNumbers = getPhoneNumbersWithIncoming()
            val sortedPhoneNumbers = phoneNumbers.sortedByDescending { phoneNumber ->
                phoneNumber.durationIncoming
            }
            topPhoneNumberWithLongestIncoming.postValue(sortedPhoneNumbers.take(COUNT_ELEMENTS_IN_RV))
        }

    }

    private suspend fun getPhoneNumbersWithIncoming() : List<PhoneNumber> {
        return suspendCoroutine { continuation ->
            val phoneNumbers = mainRepository.getAllPhoneNumbers()
            val filtersPhoneNumber = phoneNumbers.filter { phoneNumber ->
                phoneNumber.counterIncoming > 0
            }
            continuation.resume(filtersPhoneNumber)
        }
    }

}