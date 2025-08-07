package com.devansh.common.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class Interstitial @JvmOverloads constructor(
    context: Context,
    val interstitialId: String = "",
    val rewardInterstitialId: String = "",
    val isInterstitialAdActive: Boolean = false,
    val isRewardInterstitialAdActive: Boolean = false,
    val adsTimeInterval: Long = 0
) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var lastTimeStampForInter: Long = 0

    init {
        lastTimeStampForInter = System.currentTimeMillis()
        MobileAds.initialize(context) { initializationStatus: InitializationStatus? -> }
        loadInterstitialAd(context)
        loadRewardedInterstitialAd(context)
    }

    fun loadInterstitialAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null,
    ) {
        if (isInterstitialAdActive.not()) return
        if (interstitialAd != null) return
        InterstitialAd.load(
            context,
            interstitialId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                    onAdLoadFailed?.invoke(adError.message)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.(interstitial)")
                    interstitialAd = ad
                    onAdLoaded?.invoke()
                }
            }
        )
    }

    fun showInterstitialAd(
        activity: Activity,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        if (!isAdReadyToShow()) return
        if (interstitialAd == null) {
            adNotAvailable?.invoke()
            loadInterstitialAd(activity)
        } else {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    onAdDismiss?.invoke()
                }
            }
            interstitialAd?.show(activity)
            lastTimeStampForInter = System.currentTimeMillis()
            interstitialAd = null
            loadInterstitialAd(activity)
        }
    }

    fun showInterstitialAdWithoutInterval(
        activity: Activity,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        if (interstitialAd == null) {
            adNotAvailable?.invoke()
            loadInterstitialAd(activity)
        } else {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    onAdDismiss?.invoke()
                }
            }
            interstitialAd?.show(activity)
            lastTimeStampForInter = System.currentTimeMillis()
            interstitialAd = null
            loadInterstitialAd(activity)
        }
    }

    fun isAdReadyToShow() =
        (System.currentTimeMillis() - lastTimeStampForInter) > adsTimeInterval

    fun showRewardInterstitialAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        if (rewardedInterstitialAd == null) {
            adNotAvailable?.invoke()
            loadRewardedInterstitialAd(activity)
        } else {
            rewardedInterstitialAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        onAdDismiss?.invoke()
                    }
                }
            rewardedInterstitialAd?.show(
                activity
            ) {
                onRewardEarned()
            }
            loadRewardedInterstitialAd(activity)
        }
    }

    private fun loadRewardedInterstitialAd(context: Context) {
        if (isRewardInterstitialAdActive.not()) return
        RewardedInterstitialAd.load(context, rewardInterstitialId,
            AdRequest.Builder().build(), object :
                RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d(TAG, "Ad was loaded.(reward)")
                    rewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    rewardedInterstitialAd = null
                }
            })
    }

    fun isInterstitialAdInitialized() = interstitialAd != null

    companion object {
        private const val TAG = "ADMOB_INTERSTITIAL"
    }
}
