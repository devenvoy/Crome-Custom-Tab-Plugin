package com.demo.hybrid


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.hybrid.databinding.ActivityMainBinding
import com.demo.hybrid.fbAds.FbAdsScreen
import com.devansh.common.FBAdManager.showExitDialog
import com.devansh.common.GoogleAdManager.showAppOpenAd
import com.devansh.common.fbads.FbNative

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var nativeAd: FbNative

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        binding.composeCheckbox.setContent {
//            AdMobScreen()
            FbAdsScreen()
        }
    }

    override fun onRestart() {
        super.onRestart()
        showAppOpenAd(this)
    }

    override fun onBackPressed() {
        showExitDialog(withAd = true)
    }
}
