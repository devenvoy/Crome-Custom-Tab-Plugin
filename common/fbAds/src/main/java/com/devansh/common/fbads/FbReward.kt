package com.devansh.common.fbads

import android.app.Activity
import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.RewardData
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.RewardedVideoAd.RewardedVideoLoadAdConfig
import com.facebook.ads.RewardedVideoAdListener


class FbReward @JvmOverloads constructor(
    context: Context,
    val rewardId: String = "",
    val isRewardAdActive: Boolean = false
) {
    private var rewardedAd: RewardedVideoAd? = null

    init {
        loadRewardAd(context)
    }

    private fun loadRewardAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if (isRewardAdActive.not()) return
        try {
            rewardedAd = RewardedVideoAd(context, rewardId)
            onAdLoaded?.invoke()
        } catch (e: Exception) {
            onAdLoadFailed?.invoke(e.message ?: "")
        }
    }

    fun showRewardAd(
        activity: Activity,
        onAdLoaded: (() -> Unit)? = null,
        failedCallBack: ((String) -> Unit)? = null,
        onRewardEarned: () -> Unit
    ) {
        val loadAdConfig: RewardedVideoLoadAdConfig? =
            rewardedAd?.buildLoadAdConfig()
                ?.withAdListener(
                    object : RewardedVideoAdListener {
                        override fun onError(p0: Ad?, p1: AdError?) {
                            failedCallBack?.invoke(p1?.errorMessage ?: "")
                        }

                        override fun onAdLoaded(p0: Ad?) {
                            onAdLoaded?.invoke()
                            rewardedAd?.show()
                        }

                        override fun onRewardedVideoCompleted() = onRewardEarned()

                        override fun onAdClicked(p0: Ad?) {}
                        override fun onLoggingImpression(p0: Ad?) {}
                        override fun onRewardedVideoClosed() {
                            loadRewardAd(context = activity,onAdLoaded,failedCallBack)
                        }
                    }
                )
                ?.withFailOnCacheFailureEnabled(true)
                ?.withRewardData(RewardData("YOUR_USER_ID", "YOUR_REWARD", 10))
                ?.build()
        rewardedAd?.loadAd(loadAdConfig)
    }

    companion object {
        private const val TAG = "FBAD_REWARD"
    }
}
