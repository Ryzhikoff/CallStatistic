package evgeniy.ryzhikov.callstatistics.di
import dagger.Component
import evgeniy.ryzhikov.callstatistics.di.modules.DatabaseModule
import evgeniy.ryzhikov.callstatistics.di.modules.DomainModule
import evgeniy.ryzhikov.callstatistics.view.customview.YandexBanner
import evgeniy.ryzhikov.callstatistics.viewmodel.HomeFragmentViewModel
import evgeniy.ryzhikov.callstatistics.viewmodel.IncomingFragmentViewModel
import evgeniy.ryzhikov.callstatistics.viewmodel.StatByPeriodViewModel
import evgeniy.ryzhikov.callstatistics.viewmodel.UpdateDBViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DomainModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {
    fun inject(updateDBViewModel: UpdateDBViewModel)
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(incomingFragmentViewModel: IncomingFragmentViewModel)
    fun inject(yandexBanner: YandexBanner)
    fun inject(statByPeriodViewModel: StatByPeriodViewModel)
}