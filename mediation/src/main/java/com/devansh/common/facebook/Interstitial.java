package com.devansh.common.facebook;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAd;
import com.devansh.common.facebook.AdListener.InterstitialListener;

import kotlin.jvm.functions.Function0;

public class Interstitial {

    private final InterstitialAd interstitialAd;
    private final String TAG = "interstitial".toUpperCase();
    public Interstitial(Context ctx, String placementId, Function0<Void> function) {
        String placementId1 = placementId != null ? placementId : "481369655656275_481400692319838"; // Default fallback
        InterstitialListener listener = new InterstitialListener(TAG) {
            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "ad is loaded and ready to be displayed!");
                // Show the ad
                function.invoke();
            }
        };
        interstitialAd = new InterstitialAd(ctx, placementId1);
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(listener).build());
    }
    public void show() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        } else {
            Log.w(TAG, "Interstitial ad not ready.");
        }
    }

    public boolean isAdReady() {
        return interstitialAd != null && interstitialAd.isAdLoaded();
    }

}
