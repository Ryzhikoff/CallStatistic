package evgeniy.ryzhikov.callstatistics.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncomingFragmentViewModel : ViewModel() {

    val phoneListLiveData = MutableLiveData<List<PhoneNumber>>()

    var totalNumber = 0
        private set
    var totalTalks = 0
        private set

    @Inject
    lateinit var mainRepository: MainRepository

    init {
        App.instance.appComponent.inject(this)
    }

    fun getPhoneList() {
        viewModelScope.launch {
            val phoneNumbers = mainRepository.getAllPhoneNumbers()

            val filtersPhoneNumber = phoneNumbers.filter { phoneNumber ->
                phoneNumber.counterIncoming > 0
            }
            filtersPhoneNumber.forEach { phoneNumber ->
                totalTalks += phoneNumber.counterIncoming
            }

            val sortedPhoneNumbers = filtersPhoneNumber.sortedByDescending { phoneNumber ->
                phoneNumber.counterIncoming
            }

            totalNumber = phoneNumbers.size

            phoneListLiveData.postValue(sortedPhoneNumbers)
        }
    }
}