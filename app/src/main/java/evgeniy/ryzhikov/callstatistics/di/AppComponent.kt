package evgeniy.ryzhikov.callstatistics.di
import dagger.Component
import evgeniy.ryzhikov.callstatistics.di.modules.DatabaseModule
import evgeniy.ryzhikov.callstatistics.di.modules.DomainModule
import evgeniy.ryzhikov.callstatistics.ui.backup.BackupFragment
import evgeniy.ryzhikov.callstatistics.ui.customview.YandexBanner
import evgeniy.ryzhikov.callstatistics.ui.home.HomeFragmentViewModel
import evgeniy.ryzhikov.callstatistics.ui.services.UpdateDBService
import evgeniy.ryzhikov.callstatistics.ui.statistic.StatByPeriodViewModel
import evgeniy.ryzhikov.callstatistics.ui.type_calls.TypeCallsViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DomainModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(incomingFragmentViewModel: TypeCallsViewModel)
    fun inject(yandexBanner: YandexBanner)
    fun inject(statByPeriodViewModel: StatByPeriodViewModel)
    fun inject(updateDBService: UpdateDBService)
    fun inject(backupFragment: BackupFragment)
}