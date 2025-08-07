package com.devansh.common.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.devansh.commons.R
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.nirav.commons.ads.utils.getAdaptiveBannerWidth


class Banner @JvmOverloads constructor(val bId: String = "", val isBannerAdActive: Boolean = false) {

    private var bannerAd: AdView? = null

    fun getView(): AdView? = bannerAd

    fun showBannerAd(frameLayout: FrameLayout) {
        if (isBannerAdActive.not()) return
        frameLayout.addShimmerForBanner()
        val adView = AdView(frameLayout.context)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = bId
        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("TAG11111", "onAdFailedToLoad: " + p0.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
                Log.e("TAG11111", "onAdLoaded: ")
            }
        }
    }

    fun showAdaptiveBannerAd(
        frameLayout: FrameLayout,
        isCollapsable: Boolean = false,
        isBottom: Boolean = true
    ) {
        if (isBannerAdActive.not()) return
        frameLayout.addShimmerForBanner()
        val adView = AdView(frameLayout.context)
        adView.setAdSize(frameLayout.context.getAdaptiveBannerWidth())
        adView.adUnitId = bId
        val adRequest = if (isCollapsable) {
            val extras = Bundle()

            extras.putString("collapsible", if (isBottom) "bottom" else "top")
            AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        } else {
            AdRequest.Builder().build()
        }
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.e("TAG11111", "onAdFailedToLoad: " + p0.message)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                bannerAd = adView
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
                Log.e("TAG11111", "onAdLoaded: ")
            }
        }
    }

    fun showBannerAd(frameLayout: FrameLayout,isCollapsable: Boolean = false) {
        bannerAd?.let {
            if (bannerAd?.parent != null) {
                (bannerAd?.parent as ViewGroup).removeView(bannerAd)
            }
            frameLayout.addView(it)
        } ?: run {
            showAdaptiveBannerAd(
                isCollapsable = isCollapsable,
                isBottom = true,
                frameLayout = frameLayout
            )
        }
    }

    fun isBannerEnabled() = isBannerAdActive
    fun getBannerId() = bId

    companion object{
        fun FrameLayout.addShimmerForBanner() {
            val inflater = LayoutInflater.from(this.context)
            val shimmerView = inflater.inflate(R.layout.shimmer_banner, this, false)
            removeAllViews()
            addView(shimmerView)
        }
    }
}
