package com.demo.hybrid

import android.app.Application
import com.devansh.common.CommonGoogleAdManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        FbCommonsAds.init(jsonString = jsonString, application = this) {}
        CommonGoogleAdManager.init(admobAdModel, onAdsInitialized = {}, applicationContext)
    }

    companion object {
        val admobAdModel = """
        {
          "appId": "ca-app-pub-3940256099942544~3347511713",
          "interstitialId": "ca-app-pub-3940256099942544/1033173712",
          "bannerId": "ca-app-pub-3940256099942544/6300978111",
          "nativeId": "ca-app-pub-3940256099942544/2247696110",
          "appOpenId": "ca-app-pub-3940256099942544/9257395921",
          "rewardId": "ca-app-pub-3940256099942544/5354046379",
          "rewardInterstitialId": "ca-app-pub-3940256099942544/5354046379",
          "isAppIdActive": true,
          "isInterstitialAdActive": true,
          "isBannerAdActive": true,
          "isNativeAdActive": true,
          "isAppOpenAdActive": true,
          "isRewardAdActive": true,
          "adsTimeInterval": 30000
            }
        """.trimIndent()

        val fbAdModel = """
        {
          "appId": "2312433698835503_2650502525028617",
          "interstitialId": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
          "bannerId": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
          "nativeId": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
          "appOpenId": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
          "rewardId": "VID_HD_16_9_15S_LINK#YOUR_PLACEMENT_ID",
          "rewardInterstitialId": "VID_HD_16_9_15S_LINK#YOUR_PLACEMENT_ID",
          "isAppIdActive": true,
          "isInterstitialAdActive": true,
          "isBannerAdActive": true,
          "isNativeAdActive": true,
          "isAppOpenAdActive": true,
          "isRewardAdActive": true,
          "adsTimeInterval": 30000
        }
        """.trimIndent()
    }
}