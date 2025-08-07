package com.demo.hybrid

import android.app.Application
//import com.argon.fbadscommons.AdModel
//import com.argon.fbadscommons.FbCommonsAds
import com.devansh.common.AdModel
import com.devansh.common.CommonAdManager
import com.google.gson.Gson

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        val jsonString = Gson().toJson(fbadModel)
//        FbCommonsAds.init(jsonString = jsonString, application = this) {}
        CommonAdManager.init(admobAdModel, onAdsInitialized = {}, applicationContext)
    }

    val admobAdModel = """
        {
          "appId": "ca-app-pub-3940256099942544~3347511713",
          "interstitialId": "ca-app-pub-3940256099942544/1033173712",
          "bannerId": "ca-app-pub-3940256099942544/6300978111",
          "nativeId": "ca-app-pub-3940256099942544/2247696110",
          "appOpenId": "ca-app-pub-3940256099942544/9257395921",
          "rewardId": "ca-app-pub-3940256099942544/5354046379",
          "isAppIdActive": true,
          "isInterstitialAdActive": true,
          "isBannerAdActive": true,
          "isNativeAdActive": true,
          "isAppOpenAdActive": true,
          "isRewardAdActive": true,
          "adsTimeInterval": 30000
        }
    """.trimIndent()

    val fbadModel = AdModel(
        appId = "2312433698835503_2650502525028617", // OK for native test ad
        interstitialId = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", // Test interstitial
        nativeId = "YOUR_PLACEMENT_ID", // Test native ad
        bannerId = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", // Test banner
        rewardInterstitialId = "VID_HD_16_9_15S_LINK#YOUR_PLACEMENT_ID",
        isAppIdActive = true,
        isInterstitialAdActive = true,
        isNativeAdActive = true,
        isBannerAdActive = true,
        isAppOpenAdActive = true,
        isRewardAdActive = true,
        isRewardInterstitialAdActive = true,
        adsTimeInterval = 30000
    )
}