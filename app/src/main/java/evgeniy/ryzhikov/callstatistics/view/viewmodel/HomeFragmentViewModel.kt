package evgeniy.ryzhikov.callstatistics.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.utils.GeneralData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragmentViewModel: ViewModel() {

    val generalDataLiveData = MutableLiveData<GeneralData>()

    @Inject
    lateinit var mainRepository: MainRepository
    init {
        App.instance.appComponent.inject(this)
    }

    fun getGeneralData() {
        viewModelScope.launch {
            val deferred = async(Dispatchers.IO) {
                val phoneNumbers = mainRepository.getAllPhoneNumbers()
                GeneralData.fill(GeneralData(), phoneNumbers)
            }
            generalDataLiveData.postValue(deferred.await())
        }
    }
}