package evgeniy.ryzhikov.callstatistics.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.YandexAds
import evgeniy.ryzhikov.callstatistics.databinding.YandexBannerBinding

class YandexBanner(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    private var _binding: YandexBannerBinding? = null
    private val binding get() = _binding!!
    private var bannerAdView: BannerAdView


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.yandex_banner, this)
        _binding = YandexBannerBinding.bind(view)
        bannerAdView = binding.bannerAdView
        loadAds()
    }


    private fun loadAds() {
        val maxWidth =
            (context.resources.displayMetrics.widthPixels / context.resources.displayMetrics.density).toInt()
        bannerAdView.setAdUnitId(YandexAds.getBannerAdUnitId())
        bannerAdView.setAdSize(
            BannerAdSize.stickySize(
                context,
                maxWidth
            )
        )


        val adRequest = AdRequest.Builder().build()
//        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
//            override fun onAdLoaded() {
//            }
//
//            override fun onAdFailedToLoad(p0: AdRequestError) {
//            }
//
//            override fun onAdClicked() {
//            }
//
//            override fun onLeftApplication() {
//            }
//
//            override fun onReturnedToApplication() {
//            }
//
//            override fun onImpression(p0: ImpressionData?) {
//            }
//
//        })

        bannerAdView.loadAd(adRequest)
    }


}