package com.argon.fbadscommons

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.facebook.ads.*
import com.google.gson.Gson

object FbCommonsAds {

    private lateinit var adModel: AdModel

    private var nativeAd: NativeBannerAd? = null
    var interstitialAd: InterstitialAd? = null
    var rewardedInterstitialAd: RewardedInterstitialAd? = null

    //    var rewardedAd: RewardedAd? = null
    private var bannerAd: AdView? = null
    var lastTimeStampForInter: Long = 0

    fun isAdReadyToShow() =
        (System.currentTimeMillis() - lastTimeStampForInter) > adModel.adsTimeInterval

    fun init(
        jsonString: String,
        application: Application,
        isTestMode: Boolean = true,
        onAdsInitialized: () -> Unit
    ) {
        val gson = Gson()
        val model = gson.fromJson(jsonString, AdModel::class.java)
        Log.e("FB_AD", "init: $model")
        this.adModel = model
        if (adModel.isAppIdActive) {
            AudienceNetworkAds.initialize(application.applicationContext)
            // if app release change to false
            AdSettings.setTestMode(isTestMode)
            // Example for setting the SDK to crash when in debug mode
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE)

            loadInterstitialAds(application)
            loadRewardedInterstitialAds(application)
            loadNativeAd(application)
//                    loadRewardAd(context)
            onAdsInitialized()
//                    if (CommonAdManager.adModel.isAppOpenAdActive) {
//                        AppOpenAdManager(application, CommonAdManager.adModel.appOpenId, activity)
//                    }
        }
    }


    fun loadInterstitialAds(context: Context) {
        interstitialAd = InterstitialAd(context, adModel.interstitialId)

        val adListener = object : InterstitialAdListener {
            override fun onError(ad: Ad?, adError: AdError?) {
                Log.e("FB_AD", "Interstitial Ad failed: ${adError?.errorMessage}")
            }

            override fun onAdLoaded(ad: Ad?) {
                Log.d("FB_AD", "Interstitial Ad is loaded and ready to be shown")
            }

            override fun onAdClicked(ad: Ad?) {}

            override fun onLoggingImpression(ad: Ad?) {}

            override fun onInterstitialDisplayed(ad: Ad?) {}

            override fun onInterstitialDismissed(ad: Ad?) {
                interstitialAd = null
            }
        }

        interstitialAd?.loadAd(
            interstitialAd?.buildLoadAdConfig()
                ?.withAdListener(adListener)
                ?.build()
        )
    }

    fun Context.showInterstitialAds(afterSomeCode: () -> Unit) {
        if (interstitialAd != null && interstitialAd?.isAdLoaded == true) {
            interstitialAd?.buildLoadAdConfig()?.withAdListener(object : InterstitialAdListener {
                override fun onError(ad: Ad?, adError: AdError?) {
                    longToastShow("${adError?.errorMessage}")
                }

                override fun onAdLoaded(ad: Ad?) {
                    longToastShow("onAdLoaded")
                }

                override fun onAdClicked(ad: Ad?) {
                    longToastShow("onAdClicked")
                }

                override fun onLoggingImpression(ad: Ad?) {
                    longToastShow("onLoggingImpression")
                }

                override fun onInterstitialDisplayed(ad: Ad?) {
                    longToastShow("onInterstitialDisplayed")
                }

                override fun onInterstitialDismissed(ad: Ad?) {
                    interstitialAd = null
                    afterSomeCode()
                }

            })?.build()
            interstitialAd?.show()
        } else {
            afterSomeCode()
        }
    }

    fun loadNativeAd(
        context: Context,
        onAdLoaded: ((Ad?) -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if (adModel.isNativeAdActive) {
            if (nativeAd == null) {
                nativeAd = NativeBannerAd(context, adModel.nativeId)

                val nativeAdListener = object : NativeAdListener {
                    override fun onError(ad: Ad?, adError: AdError?) {
                        context.longToastShow("${adError?.errorMessage}")
                        onAdLoadFailed?.invoke(adError?.errorMessage ?: "Something went wrong")
                        nativeAd = null
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        if (nativeAd != null && nativeAd!!.isAdLoaded) {
                            Log.d("FB_AD", "Ad loaded successfully")
                            onAdLoaded?.invoke(ad)
                        }
                    }

                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                    override fun onMediaDownloaded(ad: Ad?) {}
                }
                nativeAd?.loadAd(
                    nativeAd?.buildLoadAdConfig()
                        ?.withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        ?.withAdListener(nativeAdListener)
                        ?.build()
                )
            } else {
                Log.d("FB_AD", "Native ad already loaded or loading")
                onAdLoaded?.invoke(nativeAd)
            }
        }
    }

    fun FrameLayout.loadAndShowNativeAd(context: Context, isBig: Boolean = true) {
        if (adModel.isNativeAdActive.not()) return
        addShimmer()
        loadNativeAd(
            context = context,
            onAdLoaded = { ad ->
                if (nativeAd == null || nativeAd != ad) {
                    return@loadNativeAd
                }
                if (nativeAd!!.isAdInvalidated) {
                    return@loadNativeAd
                }
                context.showSmallNativeAd(this, nativeAd!!)
            },
            onAdLoadFailed = {
                removeAllViews()
            }
        )
    }

    fun Context.showSmallNativeAd(
        frameLayout: FrameLayout?,
        nativeAd: NativeBannerAd
    ) {
        if (isNetworkAvailable(this)) {
            val inflater = LayoutInflater.from(this)
            val nativeAdLayout = inflater.inflate(
                R.layout.layout_small_native_ad_mob_with_media,
                null
            ) as NativeAdLayout

            nativeAd.unregisterView()

            try {
                // Create native UI using the ad metadata.
//                val nativeAdIcon = adView.findViewById<MediaView>(R.id.native_ad_icon)
                val nativeAdTitle = nativeAdLayout.findViewById<TextView>(R.id.adTitle)
                val nativeAdMedia = nativeAdLayout.findViewById<MediaView>(R.id.mediaView)
//                val nativeAdSocialContext =
//                    adView.findViewById<TextView>(R.id.native_ad_social_context)
                val nativeAdBody = nativeAdLayout.findViewById<TextView>(R.id.adDescription)
                val sponsoredLabel = nativeAdLayout.findViewById<TextView>(R.id.adAdvertiser)
                val nativeAdCallToAction =
                    nativeAdLayout.findViewById<Button>(R.id.callToAction)

                nativeAdTitle.text = nativeAd.advertiserName
                nativeAdBody.text = nativeAd.adBodyText
//                nativeAdSocialContext.text = nativeAd.adSocialContext
                nativeAdCallToAction.visibility =
                    if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                nativeAdCallToAction.text = nativeAd.adCallToAction
                sponsoredLabel.text = nativeAd.sponsoredTranslation

                // Create a list of clickable views
                val clickableViews = ArrayList<View>()
                clickableViews.add(nativeAdTitle)
                clickableViews.add(nativeAdCallToAction)
//                clickableViews.add(nativeAdIcon)

                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(
                    nativeAdLayout, nativeAdMedia, clickableViews
                )

                if (frameLayout != null) {
                    frameLayout.visibility = View.VISIBLE
                    frameLayout.removeAllViews()
                    frameLayout.addView(nativeAdLayout)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.message)
            }
        }
    }

    fun FrameLayout.addShimmer() {
        val inflater = LayoutInflater.from(this.context)
        val shimmerView = inflater.inflate(
            R.layout.simmer_layout_big, null
        )
        removeAllViews()
        addView(shimmerView)
    }

    fun FrameLayout.addShimmerForBanner() {
        val inflater = LayoutInflater.from(this.context)
        val shimmerView = inflater.inflate(
            R.layout.shimmer_banner,
            null
        )
        removeAllViews()
        addView(shimmerView)
    }


    fun loadRewardedInterstitialAds(context: Context) {
        rewardedInterstitialAd = RewardedInterstitialAd(context, adModel.rewardInterstitialId)

        val adListener = object : RewardedInterstitialAdListener {
            override fun onError(ad: Ad?, adError: AdError?) {
                Log.e("FB_AD", "RewardedInterstitial Ad failed: ${adError?.errorMessage}")
            }

            override fun onAdLoaded(ad: Ad?) {
                Log.d("FB_AD", "RewardedInterstitial Ad is loaded and ready to be shown")
            }

            override fun onAdClicked(p0: Ad?) {}

            override fun onLoggingImpression(p0: Ad?) {}

            override fun onRewardedInterstitialCompleted() {}

            override fun onRewardedInterstitialClosed() {}
        }
        rewardedInterstitialAd?.loadAd(
            rewardedInterstitialAd?.buildLoadAdConfig()
                ?.withAdListener(adListener)
                ?.build()
        )
    }

    fun Context.showRewardedInterstitialAds(dismissCallback: () -> Unit) {
        if (rewardedInterstitialAd != null && rewardedInterstitialAd?.isAdLoaded == true) {
            rewardedInterstitialAd?.buildLoadAdConfig()?.withAdListener(object :
                RewardedInterstitialAdListener {
                override fun onError(ad: Ad?, adError: AdError?) {
                    longToastShow("${adError?.errorMessage}")
                }

                override fun onAdLoaded(ad: Ad?) {}

                override fun onAdClicked(ad: Ad?) {}

                override fun onLoggingImpression(ad: Ad?) {}

                override fun onRewardedInterstitialCompleted() {
                    rewardedInterstitialAd = null
                }

                override fun onRewardedInterstitialClosed() {
                    rewardedInterstitialAd = null
                    loadRewardedInterstitialAds(context = this@showRewardedInterstitialAds)
                    dismissCallback()
                }

            })?.build()
            rewardedInterstitialAd?.show()
        } else {
            longToastShow("Ads is not Loaded")
        }
    }


    fun FrameLayout.loadNativeBannerAds(
        type: BannerAdType,
        isCustomLayout : Boolean,
    ){
        val nativeBannerAd = NativeBannerAd(this.context, adModel.bannerId)
        addShimmerForBanner()
        val nativeAdListener = object : NativeAdListener {
            override fun onError(ad: Ad?, adError: AdError?) {
                this@loadNativeBannerAds.context.longToastShow("${adError?.errorMessage}")
            }
            override fun onAdLoaded(ad: Ad?) {
                if (isCustomLayout){
                    customNativeBannerLayout(nativeBannerAd)
                }else {
                    val viewAttributes = NativeAdViewAttributes(this@loadNativeBannerAds.context)
                        .setBackgroundColor(Color.BLACK)
                        .setTitleTextColor(Color.WHITE)
                        .setDescriptionTextColor(Color.LTGRAY)
                        .setButtonColor(Color.WHITE)
                        .setButtonTextColor(Color.BLACK)

                    val adView = NativeBannerAdView.render(
                        this@loadNativeBannerAds.context,
                        nativeBannerAd,
                        type.type,
                        viewAttributes
                    )
                    removeAllViews()
                    addView(adView)
                }
            }
            override fun onAdClicked(ad: Ad?) {}
            override fun onLoggingImpression(ad: Ad?) {}
            override fun onMediaDownloaded(ad: Ad?) {}

        }
        nativeBannerAd.loadAd(
            nativeBannerAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build()
        )
    }

    private fun FrameLayout.customNativeBannerLayout(nativeBannerAd: NativeBannerAd) {

        // Unregister last ad
        nativeBannerAd.unregisterView()

        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        val nativeAdLayout = LayoutInflater.from(this.context).inflate(
            R.layout.native_banner_ad_container,
            this,
            false
        )

        // Add the AdChoices icon
        val adChoicesContainer = nativeAdLayout.findViewById<RelativeLayout>(R.id.ad_choices_container)
        val adOptionsView =
            AdOptionsView(this.context, nativeBannerAd, nativeAdLayout as NativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdTitle = nativeAdLayout.findViewById<TextView>(R.id.native_ad_title)
        val nativeAdSocialContext = nativeAdLayout.findViewById<TextView>(R.id.native_ad_social_context)
        val sponsoredLabel = nativeAdLayout.findViewById<TextView>(R.id.native_ad_sponsored_label)
        val nativeAdIconView = nativeAdLayout.findViewById<MediaView>(R.id.native_icon_view)
        val nativeAdCallToAction = nativeAdLayout.findViewById<Button>(R.id.native_ad_call_to_action)

        // Set the Text.
        nativeAdCallToAction.text = nativeBannerAd.adCallToAction
        nativeAdCallToAction.visibility =
            if (nativeBannerAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdTitle.text = nativeBannerAd.advertiserName
        nativeAdSocialContext.text = nativeBannerAd.adSocialContext
        sponsoredLabel.text = nativeBannerAd.sponsoredTranslation

        // Register the Title and CTA button to listen for clicks.
        val clickableViews = ArrayList<View>()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)
        nativeBannerAd.registerViewForInteraction(nativeAdLayout, nativeAdIconView, clickableViews)
    }


    private fun isNetworkAvailable(c: Context): Boolean {
        val manager = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable = false
        if (networkInfo != null && networkInfo.isConnected) {
            isAvailable = true
        }
        return isAvailable
    }

}