package com.argon.fbadscommons

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize
import com.facebook.ads.AdView

fun showBannerAds(
    context: Context,
    adsBannerContainer: LinearLayout,
    placementId: String,
    adSize: AdSize
): AdView {

    val adView = AdView(context, placementId, adSize)
    adsBannerContainer.addView(adView)

    val adListener = object : AdListener {
        override fun onError(ad: Ad?, adError: AdError?) {
            context.longToastShow("error" + adError?.errorMessage)
        }

        override fun onAdLoaded(ad: Ad?) {
            context.longToastShow("onAdLoaded")

        }

        override fun onAdClicked(ad: Ad?) {
            context.longToastShow("onAdClicked")

        }

        override fun onLoggingImpression(ad: Ad?) {
            context.longToastShow("onLoggingImpression")

        }
    }
    adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
    return adView
}

fun Context.longToastShow(msg: String) {
    Log.e("FB_AD", "longToastShow: $msg")
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}