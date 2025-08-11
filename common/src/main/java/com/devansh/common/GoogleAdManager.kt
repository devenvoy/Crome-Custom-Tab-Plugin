package com.devansh.common

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import com.devansh.common.core.AdModel
import com.devansh.common.googleads.AppOpenAdManager
import com.devansh.common.googleads.Banner
import com.devansh.common.googleads.CommonGdprDialog
import com.devansh.common.googleads.Interstitial
import com.devansh.common.googleads.Native
import com.devansh.common.googleads.OnShowAdCompleteListener
import com.devansh.common.googleads.Reward
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson

object GoogleAdManager {
    private var adModel = AdModel()
    private var appOpenAdManager: AppOpenAdManager? = null
    private var interstitialAd: Interstitial? = null
    private var nativeAd: Native? = null
    private var rewardedAd: Reward? = null
    private var bannerAd: Banner? = null

    fun init(
        jsonString: String,
        onAdsInitialized: () -> Unit,
        context: Context
    ) {
        val gson = Gson()
        val adModel = gson.fromJson(jsonString, AdModel::class.java)
        Log.e("TAG111", "init: $adModel")
        this.adModel = adModel
        val context = context.applicationContext
        if (adModel.isAppIdActive) {
            MobileAds.initialize(context)
            setAppId(context, onAdsInitialized)
        }
    }


    fun Activity.showRewardAd(
        failedCallBack: ((String) -> Unit)? = null,
        onRewardEarned: () -> Unit
    ) {
        rewardedAd?.showRewardAd(this, failedCallBack, onRewardEarned)
    }

    fun initWithGdpr(
        activity: Activity,
        jsonString: String,
        onAdsInitialized: () -> Unit,
    ) {
        val gson = Gson()
        val adModel = gson.fromJson(jsonString, AdModel::class.java)
        Log.e("TAG111", "init: $adModel")
        if (adModel.isAppIdActive) {
            CommonGdprDialog.checkGDPR(activity) {
                MobileAds.initialize(activity)
                setAppId(activity, onAdsInitialized)
            }
        }
    }

    private fun setAppId(context: Context, onAdsInitialized: () -> Unit) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({

                initInterstitial(context)

                initNative(context)

                initReward(context)

                initBanner()

                onAdsInitialized()

                if (adModel.isAppOpenAdActive) {
                    appOpenAdManager =
                        AppOpenAdManager(context, adModel.appOpenId, adModel.adsTimeInterval)
                }
            }, 1000)
        } catch (e: Exception) {
            Log.e("TAG000", "Failed to load meta-data, NameNotFound: " + e.message)
        }
    }

    private fun initReward(context: Context) {
        if (rewardedAd != null) return
        rewardedAd = Reward(
            context,
            adModel.rewardId,
            adModel.isRewardAdActive
        )
    }

    private fun initBanner() {
        if (bannerAd != null) return
        bannerAd = Banner(
            adModel.bannerId,
            adModel.isBannerAdActive
        )
    }

    private fun initNative(context: Context) {
        if (nativeAd != null) return
        nativeAd = Native(
            context,
            adModel.nativeId,
            adModel.isNativeAdActive
        )
    }

    private fun initInterstitial(context: Context) {
        if (interstitialAd != null) return
        interstitialAd = Interstitial(
            context,
            adModel.interstitialId,
            adModel.rewardInterstitialId,
            adModel.isInterstitialAdActive,
            adModel.isRewardInterstitialAdActive,
            adModel.adsTimeInterval
        )
    }


    fun showAppOpenAd(activity: Activity, onComplete: () -> Unit = {}) {
        appOpenAdManager?.showAdIfAvailable(activity, object : OnShowAdCompleteListener {
            override fun onShowAdComplete() {
                onComplete()
            }
        })
    }

    // Interstitial
    fun loadInterstitialAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null,
    ) {
        initInterstitial(context)
        interstitialAd?.loadInterstitialAd(context, onAdLoaded, onAdLoadFailed)
    }

    fun Activity.showInterstitialAd(
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        initInterstitial(this)
        interstitialAd?.showInterstitialAd(this, adNotAvailable, onAdDismiss)
    }

    fun Activity.showInterstitialAdWithoutInterval(
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        initInterstitial(this)
        interstitialAd?.showInterstitialAdWithoutInterval(this, adNotAvailable, onAdDismiss)
    }

    fun Activity.showRewardInterstitialAd(
        onRewardEarned: () -> Unit,
        adNotAvailable: (() -> Unit)? = null,
        onAdDismiss: (() -> Unit)? = null
    ) {
        initInterstitial(this)
        interstitialAd?.showRewardInterstitialAd(this, onRewardEarned, adNotAvailable, onAdDismiss)
    }

    // Banner
    fun FrameLayout.showBannerAd() {
        initBanner()
        bannerAd?.showBannerAd(this)
    }

    fun FrameLayout.showAdaptiveBannerAd(
        isCollapsable: Boolean = false,
        isBottom: Boolean = true
    ) {
        initBanner()
        bannerAd?.showAdaptiveBannerAd(this, isCollapsable, isBottom)
    }

    // NativeAd

    fun FrameLayout.showNativeAd() {
        initNative(this.context)
        nativeAd?.showNativeAd(this)
    }

    fun FrameLayout.showExitNativeAd() {
        initNative(this.context)
        nativeAd?.showExitNativeAd(this)
    }

    fun loadNativeAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        initNative(context)
        nativeAd?.loadNativeAd(context, onAdLoaded, onAdLoadFailed)
    }

    fun loadNewNativeAd(context: Context) {
        initNative(context)
        nativeAd?.loadNewNativeAd(context)
    }

    fun FrameLayout.showSmallNativeAd() {
        initNative(this.context)
        nativeAd?.loadAndShowNativeAd(this,this.context,false)
    }

    fun Activity.showExitDialog(withAd: Boolean = false) {
        initNative(this)
        nativeAd?.showExitDialog(this, withAd)
    }

    fun FrameLayout.loadAndShowNativeAd(isBig: Boolean = true) {
        initNative(this.context)
        nativeAd?.loadAndShowNativeAd(this, this.context, isBig)
    }

    fun isBannerEnabled() = adModel.isBannerAdActive

    fun getBannerId() = adModel.bannerId

    fun getAppOpenId() = adModel.appOpenId

    fun getNativeAd() = nativeAd

    fun isNativeAdInitialized(): Boolean = nativeAd?.isNativeAdInitialized() == true

    fun isInterstitialAdInitialized(): Boolean =
        interstitialAd?.isInterstitialAdInitialized() == true
}