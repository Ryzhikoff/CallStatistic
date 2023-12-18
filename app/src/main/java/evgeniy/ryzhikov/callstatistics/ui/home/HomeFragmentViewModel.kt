package evgeniy.ryzhikov.callstatistics.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneNumbers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragmentViewModel: ViewModel() {

    val consolidatedPhoneNumbers = MutableLiveData<ConsolidatedPhoneNumbers>()

    @Inject
    lateinit var mainRepository: MainRepository
    init {
        App.instance.appComponent.inject(this)
    }

    fun getGeneralData() {
        viewModelScope.launch {
            val deferred = async(Dispatchers.IO) {
                val phoneNumbers = mainRepository.getAllPhoneNumbers()
                ConsolidatedPhoneNumbers.fill(ConsolidatedPhoneNumbers(), phoneNumbers)
            }
            consolidatedPhoneNumbers.postValue(deferred.await())
        }
    }
}