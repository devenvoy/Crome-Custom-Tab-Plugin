package com.devansh.common

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.devansh.common.core.AdNetwork
import com.devansh.common.core.Ad_MODEL_STORE_KEY

object CommonAdManager {

    private var preferredNetwork: AdNetwork = AdNetwork.GOOGLE
    private var fallbackNetwork: AdNetwork? = null

    fun Context.setPreferredNetwork(preferred: AdNetwork, fallback: AdNetwork? = null) {
        preferredNetwork = preferred
        fallbackNetwork = fallback
        init(context = this, onAdsInitialized = {})
    }

    fun init(
        onAdsInitialized: () -> Unit,
        context: Context
    ) {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> {
                val jsonString = context.getSharedPreferences(Ad_MODEL_STORE_KEY, Context.MODE_PRIVATE)
                    .getString(AdNetwork.GOOGLE.name, "") ?: ""
                GoogleAdManager.init(jsonString, {
                    onAdsInitialized()
                }, context)
            }

            AdNetwork.FACEBOOK -> {
                val jsonString = context.getSharedPreferences(Ad_MODEL_STORE_KEY, Context.MODE_PRIVATE)
                    .getString(AdNetwork.FACEBOOK.name, "") ?: ""
                FBAdManager.init(jsonString, {
                    onAdsInitialized()
                }, context)
            }
        }
    }

    fun initWithGdpr(
        activity: Activity,
        onAdsInitialized: () -> Unit
    ) {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> {
                val jsonString = activity.getSharedPreferences(Ad_MODEL_STORE_KEY, Context.MODE_PRIVATE)
                    .getString(AdNetwork.GOOGLE.name, "") ?: ""
                GoogleAdManager.initWithGdpr(activity, jsonString, onAdsInitialized)
            }

            AdNetwork.FACEBOOK -> {
                val jsonString = activity.getSharedPreferences(Ad_MODEL_STORE_KEY, Context.MODE_PRIVATE)
                    .getString(AdNetwork.FACEBOOK.name, "") ?: ""
                FBAdManager.initWithGdpr(activity, jsonString, onAdsInitialized)
            }
        }
    }

    // ----------- Reward Ads -----------
    fun Activity.showRewardAd(
        failedCallBack: ((String) -> Unit)? = null,
        onRewardEarned: () -> Unit
    ) {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> GoogleAdManager.run {
                this@showRewardAd.showRewardAd(failedCallBack, onRewardEarned)
            }

            AdNetwork.FACEBOOK -> FBAdManager.run {
                this@showRewardAd.showRewardAd(failedCallBack, onRewardEarned)
            }
        }
    }

    // ----------- Interstitial Ads -----------
    fun Activity.showInterstitialAd(
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        fun tryNetwork(network: AdNetwork) {
            when (network) {
                AdNetwork.GOOGLE -> GoogleAdManager.run {
                    this@showInterstitialAd.showInterstitialAd(
                        {
                            if (fallbackNetwork != null && fallbackNetwork != network) {
                                Log.d("AdManager", "Google ad not available, trying fallback...")
                                tryNetwork(fallbackNetwork!!)
                            } else adNotAvailable?.invoke()
                        },
                        onAdDismiss
                    )
                }

                AdNetwork.FACEBOOK -> FBAdManager.run {
                    this@showInterstitialAd.showInterstitialAd(
                        {
                            if (fallbackNetwork != null && fallbackNetwork != network) {
                                Log.d("AdManager", "Facebook ad not available, trying fallback...")
                                tryNetwork(fallbackNetwork!!)
                            } else adNotAvailable?.invoke()
                        },
                        onAdDismiss
                    )
                }
            }
        }

        // Start with preferred network
        tryNetwork(preferredNetwork)
    }

    // ----------- Banner Ads -----------
    fun FrameLayout.showBannerAd() {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> GoogleAdManager.run {
                this@showBannerAd.showAdaptiveBannerAd()
            }

            AdNetwork.FACEBOOK -> FBAdManager.run {
                this@showBannerAd.showAdaptiveBannerAd()
            }
        }
    }

    // ----------- Native Ads -----------
    fun FrameLayout.showNativeAd() {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> GoogleAdManager.run {
                this@showNativeAd.showNativeAd()
            }

            AdNetwork.FACEBOOK -> FBAdManager.run {
                this@showNativeAd.showNativeAd()
            }
        }
    }

    // ----------- Exit Dialog Ads -----------
    fun Activity.showExitDialog(withAd: Boolean = true) {
        when (preferredNetwork) {
            AdNetwork.GOOGLE -> GoogleAdManager.run {
                this@showExitDialog.showExitDialog(withAd)
            }

            AdNetwork.FACEBOOK -> FBAdManager.run {
                this@showExitDialog.showExitDialog(withAd)
            }
        }
    }
}