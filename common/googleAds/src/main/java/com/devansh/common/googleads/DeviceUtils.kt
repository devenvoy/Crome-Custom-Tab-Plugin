package com.devansh.common.googleads

import android.content.Context
import com.google.android.gms.ads.AdSize

fun Context.getAdaptiveBannerWidth(): AdSize {
    val outMetrics = resources.displayMetrics
    val widthPixels = outMetrics.widthPixels.toFloat()
    val density = outMetrics.density
    val adWidth = (widthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}