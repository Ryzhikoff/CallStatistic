package evgeniy.ryzhikov.callstatistics.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.PhoneDatabase
import evgeniy.ryzhikov.callstatistics.data.dao.PhoneNumberDao
import evgeniy.ryzhikov.callstatistics.data.dao.PhoneTalkDao
import evgeniy.ryzhikov.callstatistics.utils.DATABASE_NAME
import javax.inject.Singleton

@Module
class DatabaseModule() {

    @Singleton
    @Provides
    fun providePhoneDatabase(context: Context) =
        Room.databaseBuilder(
            context,
            PhoneDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providePhoneNumberDao(phoneDatabase: PhoneDatabase) =
        phoneDatabase.phoneNumberDao()

    @Singleton
    @Provides
    fun providePhoneTalkDao(phoneDatabase: PhoneDatabase) =
        phoneDatabase.phoneTalkDao()

    @Singleton
    @Provides
    fun provideMainRepository(phoneNumberDao: PhoneNumberDao, phoneTalkDao: PhoneTalkDao) =
        MainRepository(phoneNumberDao, phoneTalkDao)
}