package com.devansh.common.fbads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAdListener

class FbInterstitial @JvmOverloads constructor(
    context: Context,
    val interstitialId: String = "",
    val rewardVideoId: String = "",
    val isInterstitialAdActive: Boolean = false,
    val isRewardVideoAdActive: Boolean = false,
    val adsTimeInterval: Long = 0
) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null
    var lastTimeStampForInter: Long = 0

    init {
        lastTimeStampForInter = System.currentTimeMillis()
        AudienceNetworkAds.initialize(context)
        loadInterstitialAd(context)
        loadRewardVideoAd(context)
    }

    fun loadInterstitialAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if (!isInterstitialAdActive) return
        interstitialAd = InterstitialAd(context, interstitialId)
        interstitialAd?.loadAd(
            interstitialAd?.buildLoadAdConfig()?.withAdListener(
                object : InterstitialAdListener {
                    override fun onError(ad: Ad?, adError: AdError) {
                        Log.d(TAG, adError.errorMessage)
                        interstitialAd = null
                        onAdLoadFailed?.invoke(adError.errorMessage)
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        Log.d(TAG, "FB Interstitial loaded")
                        onAdLoaded?.invoke()
                    }

                    override fun onInterstitialDismissed(ad: Ad?) {
                        loadInterstitialAd(context)
                    }

                    override fun onInterstitialDisplayed(ad: Ad?) {}
                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                })
                ?.build()
        )
    }

    fun showInterstitialAd(activity: Activity) {
        if (!isAdReadyToShow()) return
        if (isInterstitialAdInitialized()) {
            interstitialAd?.show()
        } else {
            showInterstitialAdWithoutInterval(activity)
        }
    }

    fun showInterstitialAdWithoutInterval(activity: Activity) {
        loadInterstitialAd(activity)
        interstitialAd?.show()
    }

    fun isAdReadyToShow() =
        (System.currentTimeMillis() - lastTimeStampForInter) > adsTimeInterval

    fun showRewardVideoAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        adNotAvailable: (() -> Unit)? = null,
    ) {
        if (rewardedVideoAd?.isAdLoaded != true || rewardedVideoAd!!.isAdInvalidated) {
            adNotAvailable?.invoke()
            loadRewardVideoAd(activity)
        } else {
            rewardedVideoAd?.show()
            onRewardEarned()
        }
    }

    private fun loadRewardVideoAd(context: Context) {
        if (!isRewardVideoAdActive) return
        rewardedVideoAd = RewardedVideoAd(context, rewardVideoId)
        rewardedVideoAd?.loadAd(
            rewardedVideoAd?.buildLoadAdConfig()?.withAdListener(object : RewardedVideoAdListener {
                override fun onError(ad: Ad?, adError: AdError) {
                    Log.d(TAG, adError.errorMessage)
                    rewardedVideoAd = null
                }

                override fun onAdLoaded(ad: Ad?) {
                    Log.d(TAG, "FB Rewarded Video loaded")
                }

                override fun onRewardedVideoCompleted() {
                    Log.d(TAG, "Reward earned")
                }

                override fun onRewardedVideoClosed() {
                    Log.d(TAG, "Reward video closed")
                    loadRewardVideoAd(context)
                }

                override fun onAdClicked(ad: Ad?) {}
                override fun onLoggingImpression(ad: Ad?) {}
            })?.build()
        )
    }

    fun isInterstitialAdInitialized() =
        interstitialAd != null && interstitialAd?.isAdLoaded == true && !interstitialAd!!.isAdInvalidated

    companion object {
        private const val TAG = "FB_INTERSTITIAL"
    }
}

