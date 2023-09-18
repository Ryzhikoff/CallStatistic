package evgeniy.ryzhikov.callstatistics.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.utils.TABLE_NAME_PHONE_TALK

@Dao
interface PhoneTalkDao {
    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK")
    fun getAllPhoneTalk(): List<PhoneTalk>

    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK WHERE phone_number LIKE :searchPhoneNumber AND date LIKE :searchData")
    fun getPhoneTalkByNumberAndDate(searchPhoneNumber: String, searchData: Long): PhoneTalk

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<PhoneTalk>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(phoneTalk: PhoneTalk)

}