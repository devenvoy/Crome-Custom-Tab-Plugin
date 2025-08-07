package com.devansh.common.facebook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devansh.commons.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import java.util.ArrayList;
import java.util.List;

public class NativeBanner {
    private final NativeBannerAd nativeBannerAd;

    public NativeBanner(Context context, String PLACEMENT_ID, View container) {
        nativeBannerAd = new NativeBannerAd(context, PLACEMENT_ID);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != nativeBannerAd || nativeBannerAd.isAdInvalidated()) return;
                inflateAd(context, nativeBannerAd, container);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        NativeAdBase.NativeAdLoadConfigBuilder loadAdConfig = nativeBannerAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener);

        nativeBannerAd.loadAd(loadAdConfig.build());
    }

    private void inflateAd(Context ctx, NativeBannerAd nativeBannerAd, View view) {
        if (ctx == null || nativeBannerAd == null || view == null) return;

        try {
            nativeBannerAd.unregisterView();
            LayoutInflater inflater = LayoutInflater.from(ctx);
            ViewGroup adContainer = (ViewGroup) view;
            adContainer.removeAllViews();

            View adView = inflater.inflate(R.layout.nativebanner, adContainer, false);
            adContainer.addView(adView);

            // Bind views
            TextView adTitle = adView.findViewById(R.id.adTitle);
            TextView adBody = adView.findViewById(R.id.adDescription);
            TextView adAdvertiser = adView.findViewById(R.id.adAdvertiser);
            Button callToAction = adView.findViewById(R.id.callToAction);
            com.facebook.ads.MediaView mediaView = adView.findViewById(R.id.mediaView);

            // Populate ad data
            adTitle.setText(nativeBannerAd.getAdvertiserName());
            adBody.setText(nativeBannerAd.getAdBodyText());
            adAdvertiser.setText(nativeBannerAd.getSponsoredTranslation());
            callToAction.setText(nativeBannerAd.getAdCallToAction());
            callToAction.setVisibility(nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);

            // Clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adTitle);
            clickableViews.add(callToAction);

            // Register interaction
            nativeBannerAd.registerViewForInteraction(adView, mediaView, clickableViews);
        } catch (Exception e) {
            Log.e("NativeBanner", "Failed to inflate native ad: " + e.getMessage(), e);
        }
    }
}

