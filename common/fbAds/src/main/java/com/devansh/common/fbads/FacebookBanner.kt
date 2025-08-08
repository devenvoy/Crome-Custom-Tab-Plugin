package com.devansh.common.fbads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize
import com.facebook.ads.AdView

class FacebookBanner @JvmOverloads constructor(
    private val bId: String = "",
    private val isBannerAdActive: Boolean = false
) {
    private var bannerAdView: AdView? = null

    fun getView(): AdView? = bannerAdView

    fun showAdaptiveBannerAd(
        frameLayout: FrameLayout,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailed: ((AdError) -> Unit)? = null
    ) {
        if (!isBannerAdActive) return

        frameLayout.addShimmerForBanner()
        val adSize = getBestFacebookAdSize(frameLayout.context)

        bannerAdView?.destroy()
        bannerAdView = AdView(frameLayout.context, bId, adSize)

        bannerAdView?.loadAd(
            bannerAdView!!.buildLoadAdConfig()
                .withAdListener(object : AdListener {
                    override fun onError(ad: Ad?, error: AdError) {
                        Log.e(TAG, "Adaptive Banner failed: ${error.errorMessage}")
                        onAdFailed?.invoke(error)
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        Log.d(TAG, "Adaptive Banner loaded")
                        frameLayout.removeAllViews()
                        frameLayout.addView(bannerAdView)
                        onAdLoaded?.invoke()
                    }

                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                })
                .build()
        )
    }

    private fun getBestFacebookAdSize(context: Context): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val widthDp = displayMetrics.widthPixels / displayMetrics.density

        return when {
            widthDp >= 728 -> AdSize.BANNER_HEIGHT_90
            widthDp >= 480 -> AdSize.BANNER_HEIGHT_50
            else -> AdSize.BANNER_HEIGHT_50
        }
    }

    fun destroyBannerAd() {
        bannerAdView?.destroy()
        bannerAdView = null
    }

    fun isBannerEnabled() = isBannerAdActive
    fun getBannerId() = bId

    companion object {
        private const val TAG = "FacebookBanner"

        fun FrameLayout.addShimmerForBanner() {
            val shimmerView = LayoutInflater.from(context).inflate(com.devansh.common.core.R.layout.shimmer_banner, this, false)
            removeAllViews()
            addView(shimmerView)
        }
    }
}
