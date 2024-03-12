package evgeniy.ryzhikov.callstatistics.data.dao

import androidx.core.app.NotificationCompat.CallStyle.CallType
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

    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK WHERE phone_number LIKE :searchPhoneNumber AND dateTime LIKE :searchData AND type LIKE :type")
    fun getPhoneTalkBy(searchPhoneNumber: String, searchData: String, @CallType type: Int): PhoneTalk?

//    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK WHERE CAST (dateTime as date) LIKE :day")
    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK WHERE date(dateTime) = :day")
    fun getPhoneTalksByDay(day: String): List<PhoneTalk>

    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK WHERE type LIKE :typeCalls ORDER BY duration DESC")
    fun getPhoneTalksByType(typeCalls: Int): List<PhoneTalk>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<PhoneTalk>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(phoneTalk: PhoneTalk)

    @Query("SELECT COUNT(*) FROM $TABLE_NAME_PHONE_TALK")
    fun getCountPhoneTalks(): Long

    @Query("SELECT * FROM $TABLE_NAME_PHONE_TALK")
    fun getAll(): List<PhoneTalk>

}