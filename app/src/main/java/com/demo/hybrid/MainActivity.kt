package com.demo.hybrid


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.argon.fbadscommons.BannerAdType
import com.argon.fbadscommons.FbCommonsAds.loadNativeBannerAds
import com.argon.fbadscommons.FbCommonsAds.showInterstitialAds
import com.demo.hybrid.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(2000)
//            binding.frameLayoutAd.loadAndShowNativeAd(this@MainActivity)
//            loadInterstitialAds(this@MainActivity)
            binding.frameLayoutAd.loadNativeBannerAds(BannerAdType.LARGE,false)

        }
        binding.someBtn.setOnClickListener {

            showInterstitialAds {  }
//            showRewardedInterstitialAds {  }
        }

    }
}