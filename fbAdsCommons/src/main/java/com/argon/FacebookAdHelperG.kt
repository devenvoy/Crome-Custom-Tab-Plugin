package com.argon

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener

class FacebookAdHelperG(private val activity: Activity) {
    private val context: Context = activity.applicationContext
    private var interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null
    private var adView: AdView? = null
    private var adContainer: LinearLayout? = null
    private var interstitialAdId: String? = null
    private var rewardedVideoAdId: String? = null
    private var bannerAdId: String? = null

    private var interstitialAdLoaded = false
    private var rewardedAdLoaded = false

    init {
        AudienceNetworkAds.initialize(context)
        Log.d("FAN", "Facebook Audience Network Initialized")
    }

    fun initAds(interstitialId: String?, rewardedId: String?, bannerId: String?) {
        this.interstitialAdId = interstitialId
        this.rewardedVideoAdId = rewardedId
        this.bannerAdId = bannerId

        loadInterstitialAd()
        loadRewardedAd()
    }

    // Interstitial
    fun loadInterstitialAd(
        onAdLoaded: () -> Unit = {},
        onAdFailedToLoad: () -> Unit = {}
    ) {
        interstitialAd = InterstitialAd(context, interstitialAdId)
        val interstitialListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad?) {
                Log.d("FAN", "Interstitial ad displayed")
            }

            override fun onInterstitialDismissed(ad: Ad?) {
                Log.d("FAN", "Interstitial ad dismissed")
                interstitialAd!!.loadAd()
            }

            override fun onError(ad: Ad?, error: AdError) {
                onAdFailedToLoad()
                Log.e("FAN", "Interstitial load failed: " + error.errorMessage)
            }

            override fun onAdLoaded(ad: Ad?) {
                interstitialAdLoaded = true
                onAdLoaded()
                Log.d("FAN", "Interstitial ad loaded")
            }

            override fun onAdClicked(ad: Ad?) {}

            override fun onLoggingImpression(ad: Ad?) {}
        }

        interstitialAd!!.loadAd(
            interstitialAd!!.buildLoadAdConfig().withAdListener(interstitialListener).build()
        )
    }

    fun showInterstitialAd() {
        if (interstitialAdLoaded && interstitialAd!!.isAdLoaded) {
            interstitialAd?.show()
            interstitialAdLoaded = false
        } else {
            Log.d("FAN", "Interstitial not loaded yet")
        }
    }

    // Rewarded
    fun loadRewardedAd() {
        rewardedVideoAd = RewardedVideoAd(context, rewardedVideoAdId)
        val rewardedVideoAdListener: RewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onError(ad: Ad?, error: AdError) {
                Log.e("FAN", "Rewarded load failed: " + error.errorMessage)
            }

            override fun onAdLoaded(ad: Ad?) {
                rewardedAdLoaded = true
                Log.d("FAN", "Rewarded ad loaded")
            }

            override fun onAdClicked(ad: Ad?) {}

            override fun onLoggingImpression(ad: Ad?) {}

            override fun onRewardedVideoCompleted() {
                Log.d("FAN", "Rewarded video completed - give reward")
            }

            override fun onRewardedVideoClosed() {
                Log.d("FAN", "Rewarded ad closed")
            }
        }
        rewardedVideoAd!!.loadAd(
            rewardedVideoAd!!.buildLoadAdConfig().withAdListener(rewardedVideoAdListener).build()
        )
    }

    fun showRewardedAd() {
        if (rewardedAdLoaded && rewardedVideoAd!!.isAdLoaded) {
            rewardedVideoAd!!.show()
            rewardedAdLoaded = false
        } else {
            Log.d("FAN", "Rewarded not loaded yet")
        }
    }

    // Banner
    fun FrameLayout.loadBannerAd(isTop: Boolean) {
        activity.runOnUiThread {
            if (adContainer != null) {
                this.removeView(adContainer)
                adContainer = null
            }
            adView = AdView(context, bannerAdId, AdSize.BANNER_HEIGHT_50)
            adContainer = LinearLayout(context)
            adContainer?.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            adContainer?.gravity = if (isTop) Gravity.TOP else Gravity.BOTTOM
            adContainer?.addView(adView)

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                if (isTop) Gravity.TOP else Gravity.BOTTOM
            )
            this.addView(adContainer, params)
            val adListener: AdListener = object : AdListener {
                override fun onError(ad: Ad?, adError: AdError) {
                    Log.e("FAN", "Banner load failed: " + adError.errorMessage)
                }

                override fun onAdLoaded(ad: Ad?) {
                    Log.d("FAN", "Banner ad loaded")
                }

                override fun onAdClicked(ad: Ad?) {}

                override fun onLoggingImpression(ad: Ad?) {}
            }
            adView!!.loadAd(adView!!.buildLoadAdConfig().withAdListener(adListener).build())
        }
    }

    fun showBannerAd() {
        adView?.visibility = View.VISIBLE
        adView?.loadAd()
    }

    fun hideBannerAd() {
        adView?.visibility = View.GONE
    }

    fun destroyAds() {
        if (interstitialAd != null) interstitialAd?.destroy()
        if (rewardedVideoAd != null) rewardedVideoAd?.destroy()
        if (adView != null) adView?.destroy()
    }
}
