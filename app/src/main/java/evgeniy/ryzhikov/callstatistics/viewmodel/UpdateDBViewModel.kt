package evgeniy.ryzhikov.callstatistics.viewmodel

import android.database.Cursor
import android.provider.CallLog
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.utils.getDataTimeISO8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class UpdateDBViewModel : ViewModel() {

    val updateFinishedLideData = MutableLiveData<Boolean>()
    val progressBarLiveData = MutableLiveData<Double>()

    @Inject
    lateinit var mainRepository: MainRepository

    init {
        App.instance.appComponent.inject(this)
    }

    fun addPhoneTalkByCursor(cursor: Cursor) {
        viewModelScope.launch(Dispatchers.IO) {
            if (cursor.moveToLast()) {
                var i = 0
                do {
                    val id =
                        cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls._ID)) ?: ""
                    val date =
                        cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls.DATE)) ?: 0
                    var number =
                        cursor.getStringOrNull(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                            ?: ""
                    val name =
                        cursor.getStringOrNull(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
                            ?: ""
                    val duration =
                        cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls.DURATION)) ?: 0
                    val type =
                        cursor.getIntOrNull(cursor.getColumnIndex(CallLog.Calls.TYPE)) ?: 0

                    number =
                        if (number != "" && number[0].toString() == "8") "+7${number.substring(1)}" else number

                    val phoneTalk = PhoneTalk(
                        phoneNumber = number,
                        contactName = name,
                        type = type,
                        duration = duration,
                        date = date
                    )

                    println(getDataTimeISO8601(phoneTalk.date))
                    if (isExistPhoneTalk(phoneTalk)) {
                        break
                    } else {
                        savePhoneTalkAndPhoneNumber(phoneTalk)
                    }
                    if (i > 10) break
                    i++
                    progressBarLiveData.postValue((1.0 * i / cursor.count) * 100)
                } while (cursor.moveToPrevious())
            }
            if (!cursor.isClosed) {
                cursor.close()
            }
            updateFinishedLideData.postValue(true)
        }
    }

    private fun savePhoneTalkAndPhoneNumber(phoneTalk: PhoneTalk) {
        val phoneNumber = getPhoneNumber(phoneTalk.phoneNumber)
        mainRepository.insertPhoneNumber(phoneNumber.addPhoneTalk(phoneTalk))
//        mainRepository.insertPhoneNumber(PhoneNumber.addPhoneTalk(phoneNumber, phoneTalk))
        mainRepository.insertPhoneTalk(phoneTalk)
    }

    private fun isExistPhoneTalk(phoneTalk: PhoneTalk) = mainRepository.isExistPhoneTalk(phoneTalk)


    private fun getPhoneNumber(number: String) = mainRepository.getPhoneNumber(number)


}