package com.devansh.common.admob

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError

class Listener(TAG: String) : AdListener() {
    var TAG: String = "ADMOB"

    init {
        this.TAG = this.TAG + TAG
    }

    override fun onAdLoaded() {
        // Code to be executed when an ad finishes loading.
        Log.d(TAG, "ad is loaded and ready to be displayed!")
    }

    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        super.onAdFailedToLoad(loadAdError)
        Log.e(TAG, "ad failed to load ErrorCode :  " + loadAdError.message)
    }

    override fun onAdOpened() {
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
        Log.d(TAG, "ad opened!!")
    }

    override fun onAdClicked() {
        super.onAdClicked()
        Log.d(TAG, "ad clicked!!")
    }

    override fun onAdImpression() {
        super.onAdImpression()
        Log.d(TAG, "ad impression!!")
    }

    override fun onAdSwipeGestureClicked() {
        super.onAdSwipeGestureClicked()
        Log.d(TAG, "ad onAdSwipeGestureClicked!!")
    }

    override fun onAdClosed() {
        // Code to be executed when when the user is about to return
        // to the app after tapping on an ad.
        Log.d(TAG, "ad closed!!")
    }
}
