package com.argon.fbadscommons

import com.facebook.ads.NativeBannerAdView

enum class BannerAdType(val type:NativeBannerAdView.Type) {
    SMALL(NativeBannerAdView.Type.HEIGHT_50),MEDIUM(NativeBannerAdView.Type.HEIGHT_100),LARGE(NativeBannerAdView.Type.HEIGHT_120)
}