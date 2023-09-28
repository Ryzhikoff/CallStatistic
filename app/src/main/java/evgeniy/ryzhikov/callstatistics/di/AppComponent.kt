package evgeniy.ryzhikov.callstatistics.di
import dagger.Component
import evgeniy.ryzhikov.callstatistics.di.modules.DatabaseModule
import evgeniy.ryzhikov.callstatistics.di.modules.DomainModule
import evgeniy.ryzhikov.callstatistics.view.customview.YandexBanner
import evgeniy.ryzhikov.callstatistics.viewmodel.HomeFragmentViewModel
import evgeniy.ryzhikov.callstatistics.viewmodel.IncomingFragmentViewModel
import evgeniy.ryzhikov.callstatistics.viewmodel.UpdateDBFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DomainModule::class,
        DatabaseModule::class
    ]
)
interface AppComponent {
    fun inject(updateDBFragmentViewModel: UpdateDBFragmentViewModel)
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)
    fun inject(incomingFragmentViewModel: IncomingFragmentViewModel)
    fun inject(yandexBanner: YandexBanner)
}