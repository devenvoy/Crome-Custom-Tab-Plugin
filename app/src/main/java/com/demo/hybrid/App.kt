package com.demo.hybrid

import android.app.Application
import com.argon.fbadscommons.AdModel
import com.argon.fbadscommons.FbCommonsAds
import com.google.gson.Gson

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val adModel = AdModel(
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
        val jsonString = Gson().toJson(adModel)
        FbCommonsAds.init(jsonString = jsonString, application = this) {

        }
    }
}