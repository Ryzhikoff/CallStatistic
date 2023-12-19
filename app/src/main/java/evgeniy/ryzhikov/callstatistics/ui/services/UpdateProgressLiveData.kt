package evgeniy.ryzhikov.callstatistics.ui.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UpdateProgressLiveData {
    private val _serviceCompleted = MutableLiveData<Int>()

    val serviceCompleted: LiveData<Int>
        get() = _serviceCompleted

    fun updateProgress(progress: Int) {
        _serviceCompleted.value = progress
    }
}