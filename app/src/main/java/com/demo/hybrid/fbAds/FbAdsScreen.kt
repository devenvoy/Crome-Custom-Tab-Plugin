package com.demo.hybrid.fbAds

import android.widget.FrameLayout
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.devansh.common.CommonAdManager.loadAndShowNativeAd
import com.devansh.common.CommonAdManager.showAdaptiveBannerAd
import com.devansh.common.CommonAdManager.showAppOpenAd
import com.devansh.common.CommonAdManager.showBannerAd
import com.devansh.common.CommonAdManager.showInterstitialAd
import com.devansh.common.CommonAdManager.showNativeAd
import com.devansh.common.CommonAdManager.showRewardAd
import com.devansh.common.CommonAdManager.showRewardInterstitialAd
import com.devansh.common.CommonAdManager.showSmallNativeAd

@Composable
fun FbAdsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val bannerView = remember {
        FrameLayout(context).apply {
            post { showBannerAd() }
        }
    }

    val nativeAdView = remember {
        FrameLayout(context).apply {
            post { showNativeAd() }
        }
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader { Title("Banner Ad") }
        item(key = "banner") { BannerAd(frameLayout = bannerView) }
        item(key = "adaptiveBanner") { AdaptiveBannerAd() }

        stickyHeader {
            Title("Native Ad")
        }
        item { NativeAd(frameLayout = nativeAdView) }
        item { BigNativeAd() }
        item { SmallNativeAd() }

        stickyHeader { Title("Interstitial Ad") }
        item { AdButton(title = "show Inter") { activity?.showInterstitialAd() } }
        item { AdButton(title = "show Rewarded Inter") { activity?.showRewardInterstitialAd({}) } }

        stickyHeader { Title("App Open Ad") }
        item { AdButton(title = "show app open") { showAppOpenAd(activity!!) } }

        stickyHeader { Title("Reward Ad") }
        item { AdButton(title = "show Rewarded") { activity?.showRewardAd { } } }
    }

}


@Composable
private fun BannerAd(modifier: Modifier = Modifier, frameLayout: FrameLayout) {
//    val context = LocalContext.current
//    val adView = remember { FrameLayout(context).apply { post{ showBannerAd() }} }
    AndroidView(
        modifier = modifier,
        factory = { frameLayout }
    )
}

@Composable
private fun AdaptiveBannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FrameLayout(ctx).apply {
                showAdaptiveBannerAd(isCollapsable = true, isBottom = true)
            }
        }
    )
}

@Composable
private fun NativeAd(modifier: Modifier = Modifier, frameLayout: FrameLayout) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
//            FrameLayout(ctx).apply {
//                showNativeAd()
//            }
            frameLayout
        }
    )
}

@Composable
private fun SmallNativeAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FrameLayout(ctx).apply {
                showSmallNativeAd(true)
            }
        }
    )
}

@Composable
private fun BigNativeAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FrameLayout(ctx).apply {
                loadAndShowNativeAd(isBig = true)
            }
        }
    )
}

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.h4)
    }
}

@Composable
fun AdButton(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClick) {
            Text(text = title, style = MaterialTheme.typography.h6)
        }
    }
}