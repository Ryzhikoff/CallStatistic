package evgeniy.ryzhikov.callstatistics.data

import evgeniy.ryzhikov.callstatistics.data.dao.PhoneNumberDao
import evgeniy.ryzhikov.callstatistics.data.dao.PhoneTalkDao
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk

class MainRepository(
    private val phoneNumberDao: PhoneNumberDao,
    private val phoneTalkDao: PhoneTalkDao
) {
    private var allPhonesNumbers = listOf<PhoneNumber>()
    private var allPhoneTalk = listOf<PhoneTalk>()

    fun getAllPhoneNumbers(): List<PhoneNumber> {
        return allPhonesNumbers.ifEmpty {
            allPhonesNumbers = phoneNumberDao.getAllPhoneNumber()
            allPhonesNumbers
        }
    }

    fun getAllPhoneTalk(): List<PhoneTalk> {
        return allPhoneTalk.ifEmpty {
            allPhoneTalk = phoneTalkDao.getAllPhoneTalk()
            allPhoneTalk
        }
    }

    fun insertListPhoneNumber(list: List<PhoneNumber>) {
        phoneNumberDao.insertAll(list)
    }

    fun insertPhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumberDao.insert(phoneNumber)
    }

    fun insertListPhoneTalk(list: List<PhoneTalk>) {
        phoneTalkDao.insertAll(list)
    }

    fun insertPhoneTalk(phoneTalk: PhoneTalk) {
        phoneTalkDao.insert(phoneTalk)
    }

    fun getPhoneNumber(number: String) = phoneNumberDao.getPhoneNumber(number)

    fun isExistPhoneTalk(phoneTalk: PhoneTalk): Boolean {
        val talk = phoneTalkDao.getPhoneTalkByNumberAndDate(phoneTalk.phoneNumber, phoneTalk.date)
        return talk != null
    }

}