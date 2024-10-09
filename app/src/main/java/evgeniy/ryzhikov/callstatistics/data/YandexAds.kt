package evgeniy.ryzhikov.callstatistics.data

import evgeniy.ryzhikov.callstatistics.BuildConfig


object YandexAds {

    fun getInterstitialAdId(): String =
        if (BuildConfig.DEBUG) {
            "demo-interstitial-yandex"
        } else {
            "R-M-2956905-2"
        }

    val bannerUpdateDB: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-1"
        }
    }

    val bannerMain: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-3"
        }
    }

    val bannerStatistic: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-4"
        }
    }

    val bannerBackup: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-5"
        }
    }

    val bannerIncoming: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-6"
        }
    }

    val bannerOutgoing: String get() {
        return if (BuildConfig.DEBUG) {
            "demo-banner-yandex"
        } else {
            "R-M-2956905-7"
        }
    }

}
