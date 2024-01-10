package evgeniy.ryzhikov.callstatistics.data

import evgeniy.ryzhikov.callstatistics.BuildConfig


object YandexAds {
//    const val interstitialAdUnitId = "R-M-2577975-1"
//    const val rewardedAdUnitId = "R-M-2577975-2"
//    const val bannerAdUnitId = "R-M-2577975-4"
//    const val interstitialAdUnitId = "demo-interstitial-yandex"
//    const val rewardedAdUnitId = "demo-rewarded-yandex"
//    const val bannerAdUnitId = "demo-banner-yandex"
//    fun getBannerAdUnitId() = "demo-banner-yandex"

    fun getBannerAdUnitId(): String =
        if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-1"
        }
}
