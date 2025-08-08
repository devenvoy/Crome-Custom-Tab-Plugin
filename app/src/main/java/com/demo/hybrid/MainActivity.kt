package com.demo.hybrid


import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.demo.hybrid.App.Companion.fbAdModel
import com.demo.hybrid.databinding.ActivityMainBinding
import com.demo.hybrid.fbAds.AdButton
import com.devansh.common.core.AdModel
import com.devansh.common.fbads.FacebookBanner
import com.devansh.common.fbads.FbInterstitial
import com.devansh.common.fbads.FbNative
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var nativeAd: FbNative

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        val gson = Gson()
        val adModel = gson.fromJson(fbAdModel, AdModel::class.java)
        nativeAd = FbNative(this, adModel.nativeId, adModel.isNativeAdActive)
        val fbBanner = FacebookBanner(adModel.bannerId, adModel.isBannerAdActive)
        val inter = FbInterstitial(
            this,
            adModel.interstitialId,
            adModel.rewardInterstitialId,
            adModel.isInterstitialAdActive,
            true
        )

        binding.composeCheckbox.setContent {
//            AdMobScreen()
            Scaffold {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    AndroidView(factory = {
                        FrameLayout(it).apply {
                            fbBanner.showAdaptiveBannerAd(
                                this
                            )
                        }
                    })
                    AndroidView(factory = { FrameLayout(it).apply { nativeAd.showNativeAd(this) } })

                    AndroidView(factory = {
                        FrameLayout(it).apply {
                            nativeAd.showNativeAd(
                                this,
                                false
                            )
                        }
                    })

                    AdButton("Interstitial") {
                        inter.showInterstitialAd(this@MainActivity)
                    }

                    AdButton("Reward Interstitial") {
                        inter.showRewardVideoAd(this@MainActivity, onRewardEarned = {})
                    }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
//        showAppOpenAd(this)
    }

    override fun onBackPressed() {
        nativeAd.showExitDialog(this, true)
    }
}
