package evgeniy.ryzhikov.callstatistics.ui.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.utils.ConsolidatedPhoneTalks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatByPeriodViewModel  : ViewModel() {

    val consolidatedPhoneTalksLiveData = MutableLiveData<ConsolidatedPhoneTalks>()
    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var mainRepository: MainRepository
    init {
        App.instance.appComponent.inject(this)
    }

    fun getPhoneTalksByDay(day: String) {
        scope.launch {
            val phoneTalks = mainRepository.getPhoneTalksByDay(day)
            consolidatedPhoneTalksLiveData.postValue(ConsolidatedPhoneTalks(phoneTalks, day))
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}