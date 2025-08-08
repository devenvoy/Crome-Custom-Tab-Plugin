package com.devansh.common.googleads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.lang.ref.WeakReference
import java.util.Date


interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

class AppOpenAdManager : Application.ActivityLifecycleCallbacks {

    private var currentActivity: WeakReference<Activity>? = null
    private val adUnit: String
    var lastTimeStampForInter: Long = 0
    private var adsTimeInterval : Long = 0

    fun isAdReadyToShow() =
        (System.currentTimeMillis() - lastTimeStampForInter) > adsTimeInterval


    private val TAG = "AppOpenAdManager"

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            Log.e(TAG, "ON_RESUME: showOpen Ad")
            currentActivity?.get()?.let { showAdIfAvailable(it) }
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            Log.e("APP", "paused")
        }
    }

    constructor(application: Context, adUnit: String,adsTimeInterval : Long) {
        this.adUnit = adUnit
        this.adsTimeInterval = adsTimeInterval
        Log.e(TAG, "Ad Open ID : $adUnit")
//        if (currentActivity == null) {
//            currentActivity = activity
//        }
//        application.registerActivityLifecycleCallbacks(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
        loadAd(application)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = WeakReference(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}


    companion object {
        var appOpenAd: AppOpenAd? = null
        var isShowingAd = false
    }

    private var isLoadingAd = false
    private var loadTime: Long = 0

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adUnit,
            request,
            object : AppOpenAdLoadCallback() {

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "onAdLoaded.")
                }


                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d(TAG, "onAdFailedToLoad: " + loadAdError.message)
                }
            }
        )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                }
            }
        )
    }

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.")
            isShowingAd = false
            appOpenAd = null
            return
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false
                Log.d(TAG, "onAdDismissedFullScreenContent.")
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.message)

                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
                isShowingAd = false
                Log.d(TAG, "onAdShowedFullScreenContent.")
            }
        }
        if (isAdReadyToShow()) {
            appOpenAd?.show(activity)
            lastTimeStampForInter = System.currentTimeMillis()
            isShowingAd = true
        }
    }
}