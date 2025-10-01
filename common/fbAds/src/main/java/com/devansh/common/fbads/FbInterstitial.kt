package com.devansh.common.fbads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.RewardedInterstitialAd
import com.facebook.ads.RewardedInterstitialAdListener

class FbInterstitial @JvmOverloads constructor(
    context: Context,
    val interstitialId: String = "",
    val rewardVideoId: String = "",
    val isInterstitialAdActive: Boolean = false,
    val isRewardVideoAdActive: Boolean = false,
    val adsTimeInterval: Long = 0
) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var lastTimeStampForInter: Long = 0

    init {
        lastTimeStampForInter = System.currentTimeMillis()
        loadInterstitialAd(context)
        loadRewardedInterstitialAd(context)
    }

    fun loadInterstitialAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
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
                        onAdDismiss?.invoke()
                        loadInterstitialAd(context, onAdDismiss = onAdDismiss)
                    }

                    override fun onInterstitialDisplayed(ad: Ad?) {}
                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                })
                ?.build()
        )
    }

    fun showInterstitialAd(
        activity: Activity,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        if (!isAdReadyToShow()) return
        if (isInterstitialAdInitialized()) {
            lastTimeStampForInter = System.currentTimeMillis()
            interstitialAd?.show(interstitialAd?.buildShowAdConfig()?.build())
        } else {
            showInterstitialAdWithoutInterval(activity, adNotAvailable, onAdDismiss)
        }
    }

    fun showInterstitialAdWithoutInterval(
        activity: Activity,
        adNotAvailable: (() -> Unit)?,
        onAdDismiss: (() -> Unit)?
    ) {
        loadInterstitialAd(activity, {}, { adNotAvailable?.invoke() }, onAdDismiss)
        lastTimeStampForInter = System.currentTimeMillis()
        interstitialAd?.show()
    }

    fun isAdReadyToShow() =
        (System.currentTimeMillis() - lastTimeStampForInter) > adsTimeInterval

    fun showRewardedInterstitialAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null,
    ) {
        if (rewardedInterstitialAd == null || !rewardedInterstitialAd!!.isAdLoaded
            || rewardedInterstitialAd!!.isAdInvalidated
        ) {
            adNotAvailable?.invoke()
            loadRewardedInterstitialAd(activity, onAdDismiss)
        } else {
            lastTimeStampForInter = System.currentTimeMillis()
            rewardedInterstitialAd?.show()
            onRewardEarned()
        }
    }

    private fun loadRewardedInterstitialAd(context: Context, onAdDismiss: (() -> Unit)? = null) {
        if (!isRewardVideoAdActive) return
        rewardedInterstitialAd = RewardedInterstitialAd(context, rewardVideoId)
        rewardedInterstitialAd?.loadAd(
            rewardedInterstitialAd?.buildLoadAdConfig()
                ?.withAdListener(object : RewardedInterstitialAdListener {
                    override fun onError(ad: Ad?, adError: AdError) {
                        Log.d(TAG, adError.errorMessage)
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        Log.d(TAG, "FB Rewarded Video loaded")
                    }

                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                    override fun onRewardedInterstitialCompleted() {
                        Log.d(TAG, "Reward earned")
                    }

                    override fun onRewardedInterstitialClosed() {
                        Log.d(TAG, "Reward video closed")
                        onAdDismiss?.invoke()
                        loadRewardedInterstitialAd(context, onAdDismiss)
                    }
                })?.build()
        )
    }

    fun isInterstitialAdInitialized() =
        interstitialAd != null && interstitialAd?.isAdLoaded == true && !interstitialAd!!.isAdInvalidated

    companion object {
        private const val TAG = "FB_INTERSTITIAL"
    }
}

