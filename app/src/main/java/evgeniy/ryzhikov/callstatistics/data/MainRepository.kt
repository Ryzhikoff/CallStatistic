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

    fun insertPhoneNumber(phoneNumber: PhoneNumber) {
        phoneNumberDao.insert(phoneNumber)
    }

    fun insertPhoneTalk(phoneTalk: PhoneTalk) {
        phoneTalkDao.insert(phoneTalk)
    }

    fun getPhoneNumber(number: String): PhoneNumber {
        return  phoneNumberDao.getPhoneNumber(number) ?: PhoneNumber(phoneNumber = number)
    }

    fun isExistPhoneTalk(phoneTalk: PhoneTalk): Boolean {
        val talk = phoneTalkDao.getPhoneTalkBy(phoneTalk.phoneNumber, phoneTalk.dateTime, phoneTalk.type)
        return talk != null
    }

    fun getPhoneTalksByDay(day: String): List<PhoneTalk> {
       return phoneTalkDao.getPhoneTalksByDay(day)
    }

    fun getPhoneTalksByType(typeCalls: Int): List<PhoneTalk> = phoneTalkDao.getPhoneTalksByType(typeCalls)

    fun getCountPhoneTalk(): Long = phoneTalkDao.getCountPhoneTalks()

}