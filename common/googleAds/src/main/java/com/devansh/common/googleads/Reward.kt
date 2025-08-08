package com.devansh.common.googleads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class Reward @JvmOverloads constructor(
    context: Context,
    val rewardId: String = "",
    val isRewardAdActive: Boolean = false
) {
    private var rewardedAd: RewardedAd? = null

    init {
        MobileAds.initialize(context) { initializationStatus: InitializationStatus? -> }
        loadRewardAd(context)
    }

    private fun loadRewardAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if(isRewardAdActive.not()) return
        RewardedAd.load(
            context, rewardId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    rewardedAd = p0
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedAd = null
                    onAdLoadFailed?.invoke(p0.message)
                }
            })
    }

    fun showRewardAd(
        activity: Activity,
        failedCallBack: ((String) -> Unit)? = null,
        onRewardEarned: () -> Unit
    ) {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
        rewardedAd?.let { ad ->
            ad.show(activity) {
                Log.d(TAG, "User earned the reward.")
                onRewardEarned()
            }
            loadRewardAd(activity)
        } ?: run {
            loadRewardAd(activity)
            failedCallBack?.invoke("The rewarded ad wasn't ready yet.")
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    companion object {
        private const val TAG = "ADMOB_REWARD"
    }
}
