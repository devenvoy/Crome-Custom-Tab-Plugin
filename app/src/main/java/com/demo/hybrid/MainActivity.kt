package com.demo.hybrid


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.hybrid.admobAds.AdMobScreen
import com.demo.hybrid.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        binding.composeCheckbox.setContent {
            AdMobScreen()
        }
    }

    override fun onRestart() {
        super.onRestart()
//        showAppOpenAd(this)
    }
}
