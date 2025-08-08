package com.devansh.common.googleads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.devansh.common.core.databinding.DialogExitBinding
import com.devansh.common.core.utils.isNetworkAvailable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class Native @JvmOverloads constructor(
    context: Context,
    val nativeId: String = "",
    val isNativeAdActive: Boolean = false
) {

    private var nativeAd: NativeAd? = null

    init {
        loadNativeAd(context)
    }

    fun showNativeAd(frameLayout: FrameLayout) {
        nativeAd?.let {
            setBigNativeAd(frameLayout.context, frameLayout, true, it)
        } ?: run {
            loadAndShowNativeAd(frameLayout,frameLayout.context)
        }
    }

    fun showExitNativeAd(frameLayout: FrameLayout) {
        nativeAd?.let {
            setBigNativeAd(frameLayout.context, frameLayout, true, it, true)
        }
    }

    private fun setBigNativeAd(
        context: Context,
        frameLayout: FrameLayout?,
        isShowMedia: Boolean = true,
        nativeAd: NativeAd,
        isExitDialog: Boolean = false
    ) {
        val inflater = LayoutInflater.from(context)
        val adView: NativeAdView = if (isExitDialog) {
            inflater.inflate(R.layout.exit_dialog_native_ad, null) as NativeAdView
        } else {
            inflater.inflate(R.layout.layout_big_native_ad_mob, null) as NativeAdView
        }
        if (frameLayout != null) {
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            frameLayout.visibility = View.VISIBLE
        }
        try {
            if (isShowMedia) {
                val mediaView = adView.findViewById<MediaView>(R.id.mediaView)
                nativeAd.mediaContent?.let { mediaView.mediaContent = it }
                mediaView.setOnHierarchyChangeListener(object :
                    OnHierarchyChangeListener {
                    override fun onChildViewAdded(parent: View, child: View) {
                        if (child is ImageView) {
                            child.adjustViewBounds = true
                            child.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    }

                    override fun onChildViewRemoved(parent: View, child: View) {}
                })
                adView.mediaView = mediaView
            }
            adView.headlineView = adView.findViewById(R.id.adTitle)
            adView.bodyView = adView.findViewById(R.id.adDescription)
            adView.iconView = adView.findViewById(R.id.adIcon)
            adView.advertiserView = adView.findViewById(R.id.adAdvertiser)
            adView.callToActionView = adView.findViewById(R.id.callToAction)
            (adView.headlineView as TextView).text = nativeAd.headline
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = "\t\t\t" + nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
                adView.iconView?.visibility = View.VISIBLE
            }
            if (isShowMedia) {
                adView.mediaView?.visibility = View.VISIBLE
            } else {
                adView.mediaView?.visibility = View.GONE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            val vc = nativeAd.mediaContent?.videoController
            vc?.mute(true)
            if (vc?.hasVideoContent() == true) {
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {}
            }
            nativeAd.let { adView.setNativeAd(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "populateUnifiedNativeAdView Exception: " + e.message)
        }
    }

    fun loadNativeAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if (isNativeAdActive) {
            if (nativeAd == null) {
                val builder = AdLoader.Builder(context, nativeId)
                builder.forNativeAd { unifiedNativeAd: NativeAd ->
                    nativeAd = unifiedNativeAd
                }
                builder.withAdListener(object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        Log.d("TAG111", "onAdClosed:")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.d("TAG111", "onAdFailedToLoad: ${loadAdError.message}")
                        onAdLoadFailed?.invoke(loadAdError.message)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d("TAG111", "onAdLoaded: ")
                        onAdLoaded?.invoke()
                    }
                })
                val videoOptions = VideoOptions.Builder()
                    .setStartMuted(true)
                    .build()
                val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
                builder.withNativeAdOptions(adOptions)
                val adLoader = builder.build()
                adLoader.loadAd(AdRequest.Builder().build())
                nativeAd = null
            }
        }
    }

    fun loadNewNativeAd(context: Context) {
        nativeAd = null
        loadNativeAd(context)
    }

    fun showSmallNativeAd(
        frameLayout: FrameLayout,
        nativeAd: NativeAd,
        isShowMedia: Boolean
    ) {
        if (isNetworkAvailable(frameLayout.context)) {
            val inflater = LayoutInflater.from(frameLayout.context)
            val adView = inflater.inflate(
                R.layout.layout_small_native_ad_mob_with_media,
                null
            ) as NativeAdView
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
            try {
                if (isShowMedia) {
                    val mediaView = adView.findViewById<MediaView>(R.id.mediaView)
                    mediaView.mediaContent = nativeAd.mediaContent
                    mediaView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                        override fun onChildViewAdded(parent: View, child: View) {
                            if (child is ImageView) {
                                child.adjustViewBounds = true
                            }
                        }

                        override fun onChildViewRemoved(parent: View, child: View) {}
                    })

                    adView.mediaView = mediaView
                    adView.mediaView!!.visibility = View.VISIBLE
                } else {
                    if (adView.mediaView != null) {
                        adView.mediaView?.visibility = View.GONE
                    }
                }
                adView.headlineView = adView.findViewById(R.id.adTitle)
                adView.bodyView = adView.findViewById(R.id.adDescription)
                adView.advertiserView = adView.findViewById(R.id.adAdvertiser)
                adView.callToActionView = adView.findViewById(R.id.callToAction)
                (adView.headlineView as TextView?)!!.text = nativeAd.headline
                if (nativeAd.body == null) {
                    adView.bodyView!!.visibility = View.INVISIBLE
                } else {
                    adView.bodyView!!.visibility = View.VISIBLE
                    (adView.bodyView as TextView?)!!.text = nativeAd.body
                }
                if (nativeAd.icon == null) {
                    adView.iconView!!.visibility = View.GONE
                } else {
//                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
//                    adView.getIconView().setVisibility(View.VISIBLE);
                }

                if (nativeAd.callToAction == null) {
                    adView.callToActionView!!.visibility = View.INVISIBLE
                } else {
                    adView.callToActionView!!.visibility = View.VISIBLE
                    (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
                }

                if (nativeAd.advertiser == null) {
                    adView.advertiserView!!.visibility = View.GONE
                } else {
                    (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
                    adView.advertiserView!!.visibility = View.VISIBLE
                }

                //                adView.setNativeAd(nativeAd);
                val vc = nativeAd.mediaContent!!.videoController
                vc.mute(true)
                if (vc.hasVideoContent()) {
                    vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                        override fun onVideoEnd() {
                            super.onVideoEnd()
                        }
                    }
                }
                frameLayout.visibility = View.VISIBLE

                adView.setNativeAd(nativeAd)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.message)
            }
        }
    }


    fun showExitDialog(activity: Activity, withAd: Boolean = false) {
        val dialog = Dialog(activity)
        val binding = DialogExitBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        val back = Color.TRANSPARENT.toDrawable()
        val inset = InsetDrawable(back, 40)
        dialog.window?.setBackgroundDrawable(inset)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnYes.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }
        if (withAd) {
            showExitNativeAd(binding.frameLayoutAd)
        }
        dialog.show()
    }

    fun loadAndShowNativeAd(frameLayout: FrameLayout, context: Context, isBig: Boolean = true) {
        with(frameLayout) {
            if (isNativeAdActive.not()) return
            addShimmer()
            val builder = AdLoader.Builder(context, nativeId)
            builder.forNativeAd(NativeAd.OnNativeAdLoadedListener { unifiedNativeAd: NativeAd ->
                if (isBig) {
                    setBigNativeAd(
                        context = context,
                        frameLayout = this,
                        nativeAd = unifiedNativeAd,
                        isExitDialog = false
                    )
                } else {
                    showSmallNativeAd(this, unifiedNativeAd, true)
                }
            })
            builder.withAdListener(object : AdListener() {
                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d("TAG111", "onAdClosed:")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d("TAG111", "onAdFailedToLoad: ${loadAdError.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("TAG111", "onAdLoaded: ")
                }
            })
            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()
            val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun getNativeAd() = nativeAd

    fun isNativeAdInitialized(): Boolean {
        val headline: String? = if (nativeAd != null) nativeAd?.headline else null
        return !headline.isNullOrEmpty()
    }

    companion object {
        private const val TAG = "ADMOB_NATIVE"

        fun FrameLayout.addShimmer() {
            val inflater = LayoutInflater.from(this.context)
            val shimmerView = inflater.inflate(
                com.devansh.common.core.R.layout.shimmer_big, null
            )
            removeAllViews()
            addView(shimmerView)
        }
    }

}