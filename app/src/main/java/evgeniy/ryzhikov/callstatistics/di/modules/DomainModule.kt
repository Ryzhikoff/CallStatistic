package evgeniy.ryzhikov.callstatistics.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import evgeniy.ryzhikov.callstatistics.data.PhoneDatabase
import evgeniy.ryzhikov.callstatistics.domain.GoogleDriveHelper
import javax.inject.Singleton

@Module
class DomainModule(
    val context: Context
) {

    @Singleton
    @Provides
    fun provideContext() = context


    @Singleton
    @Provides
    fun provideGoogleDriveHelper(
        context: Context,
        database: PhoneDatabase
    ) = GoogleDriveHelper(context, database)
}