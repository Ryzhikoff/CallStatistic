package evgeniy.ryzhikov.callstatistics.ui.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UpdateProgressLiveData {
    private val _progressLiveData = MutableLiveData<Int>()

    val progressLiveData: LiveData<Int>
        get() = _progressLiveData

    fun updateProgress(progress: Int) {
        _progressLiveData.value = progress
    }
}