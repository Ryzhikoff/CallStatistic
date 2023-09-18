package evgeniy.ryzhikov.callstatistics.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.utils.TABLE_NAME_PHONE_NUMBER

@Dao
interface PhoneNumberDao {
    @Query("SELECT * FROM $TABLE_NAME_PHONE_NUMBER")
    fun getAllPhoneNumber(): List<PhoneNumber>

    @Query("SELECT * FROM $TABLE_NAME_PHONE_NUMBER WHERE phone_number LIKE :number")
    fun getPhoneNumber(number: String): PhoneNumber

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<PhoneNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneNumber: PhoneNumber)
}