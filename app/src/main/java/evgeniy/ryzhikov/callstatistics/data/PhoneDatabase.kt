package evgeniy.ryzhikov.callstatistics.data

import androidx.room.Database
import androidx.room.RoomDatabase
import evgeniy.ryzhikov.callstatistics.data.dao.PhoneNumberDao
import evgeniy.ryzhikov.callstatistics.data.dao.PhoneTalkDao
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk

import evgeniy.ryzhikov.callstatistics.utils.DATABASE_VERSION

@Database(
    entities = [
        PhoneNumber::class,
        PhoneTalk::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class PhoneDatabase : RoomDatabase() {
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun phoneTalkDao(): PhoneTalkDao
}