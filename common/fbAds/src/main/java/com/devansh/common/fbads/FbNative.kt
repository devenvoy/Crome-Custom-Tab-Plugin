package com.devansh.common.fbads

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.devansh.common.core.databinding.DialogExitBinding
import com.devansh.common.core.databinding.ShimmerBigBinding
import com.devansh.common.core.databinding.ShimmerSmallBinding
import com.devansh.common.fbads.databinding.ExitDialogNativeFbAdBinding
import com.devansh.common.fbads.databinding.LayoutBigNativeFbAdBinding
import com.devansh.common.fbads.databinding.LayoutSmallNativeFbAdWithMediaBinding
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.MediaView
import com.facebook.ads.MediaViewListener
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdBase.NativeComponentTag
import com.facebook.ads.NativeAdListener

class FbNative @JvmOverloads constructor(
    context: Context,
    val nativeId: String = "",
    val isNativeAdActive: Boolean = false
) {

    private var nativeAd: NativeAd? = null

    init {
        loadNativeAd(context)
    }


    fun loadNativeAd(
        context: Context,
        onAdLoaded: (() -> Unit)? = null,
        onAdLoadFailed: ((String) -> Unit)? = null
    ) {
        if (!isNativeAdActive) return
        if (nativeAd != null) return

        nativeAd = NativeAd(context, nativeId)
        nativeAd?.loadAd(
            nativeAd?.buildLoadAdConfig()
                ?.withAdListener(object : NativeAdListener {
                    override fun onError(ad: Ad?, adError: AdError) {
                        Log.d(TAG, "FB Native onError: ${adError.errorMessage}")
                        nativeAd = null
                        onAdLoadFailed?.invoke(adError.errorMessage)
                    }

                    override fun onAdLoaded(ad: Ad?) {
                        Log.d(TAG, "FB Native onAdLoaded")
                        if (ad != nativeAd) return
                        onAdLoaded?.invoke()
                    }

                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                    override fun onMediaDownloaded(ad: Ad?) {}
                })
                ?.build()
        )
    }

    fun loadNewNativeAd(context: Context) {
        nativeAd = null
        loadNativeAd(context)
    }

    fun showNativeAd(frameLayout: FrameLayout, isBig: Boolean = true) {
        frameLayout.addShimmer(isBig)
        if (isNativeAdInitialized()) {
            if (isBig) {
                setBigNativeAd(frameLayout, nativeAd!!, true)
            } else {
                showSmallNativeAd(frameLayout, nativeAd!!, true)
            }
        } else {
            loadAndShowNativeAd(frameLayout, frameLayout.context, isBig)
        }
    }

    fun showExitNativeAd(frameLayout: FrameLayout) {
        nativeAd?.let {
            setBigNativeAd(
                frameLayout = frameLayout,
                nativeAd = it,
                isShowMedia = true,
                isExitDialog = true
            )
        }
    }

    private fun setBigNativeAd(
        frameLayout: FrameLayout,
        nativeAd: NativeAd,
        isShowMedia: Boolean = true,
        isExitDialog: Boolean = false
    ) {
        val inflater = LayoutInflater.from(frameLayout.context)

        val adView = if (isExitDialog) {
            ExitDialogNativeFbAdBinding.inflate(inflater).root
        } else {
            LayoutBigNativeFbAdBinding.inflate(inflater).root
        }

        frameLayout.apply {
            removeAllViews()
            addView(adView)
            visibility = View.VISIBLE
        }

        try {
            val adIconView = adView.findViewById<ImageView?>(R.id.adIcon)
            val adTitle = adView.findViewById<TextView?>(R.id.adTitle)
            val adDescription = adView.findViewById<TextView?>(R.id.adDescription)
            val adAdvertiser = adView.findViewById<TextView?>(R.id.adAdvertiser)
            val callToAction = adView.findViewById<Button?>(R.id.callToAction)
            val mediaView = adView.findViewById<MediaView?>(R.id.mediaView)

            adTitle?.text = nativeAd.adHeadline

            adAdvertiser?.apply {
                isVisible = nativeAd.advertiserName.isNullOrEmpty()
                text = nativeAd.advertiserName
            }

            adDescription?.apply {
                isVisible = nativeAd.adBodyText.isNullOrEmpty()
                text = nativeAd.adBodyText
            }

            callToAction?.apply {
                isVisible = nativeAd.hasCallToAction()
                text = nativeAd.adCallToAction
            }

            if (isShowMedia && mediaView != null) {
                mediaView.setListener(getMediaViewListener())
                mediaView.visibility = View.VISIBLE
            } else {
                mediaView?.visibility = View.GONE
            }

            val clickableViews = mutableListOf<View>()
            adIconView?.let { clickableViews.add(it) }
            mediaView?.let { clickableViews.add(it) }
            callToAction?.let { clickableViews.add(it) }

            if (mediaView != null && adIconView != null) {
                nativeAd.registerViewForInteraction(adView, mediaView, adIconView, clickableViews)
            }

            adIconView?.let { NativeComponentTag.tagView(it, NativeComponentTag.AD_ICON) }
            adTitle?.let { NativeComponentTag.tagView(it, NativeComponentTag.AD_TITLE) }
            adDescription?.let { NativeComponentTag.tagView(it, NativeComponentTag.AD_BODY) }
            callToAction?.let {
                NativeComponentTag.tagView(it, NativeComponentTag.AD_CALL_TO_ACTION)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error binding Facebook Native Ad: ${e.message}", e)
        }
    }

    fun showSmallNativeAd(frameLayout: FrameLayout, nativeAd: NativeAd, isShowMedia: Boolean) {
        val inflater = LayoutInflater.from(frameLayout.context)
        val adView = LayoutSmallNativeFbAdWithMediaBinding.inflate(inflater)

        frameLayout.removeAllViews()
        frameLayout.addView(adView.root)

        try {
            adView.adTitle.text = nativeAd.advertiserName
            val bodyView = adView.adDescription
            if (nativeAd.adBodyText.isNullOrEmpty()) {
                bodyView.visibility = View.GONE
            } else {
                bodyView.text = nativeAd.adBodyText
                bodyView.visibility = View.VISIBLE
            }
            val ctaButton = adView.callToAction
            if (nativeAd.adCallToAction.isNullOrEmpty()) {
                ctaButton.visibility = View.GONE
            } else {
                ctaButton.text = nativeAd.adCallToAction
                ctaButton.visibility = View.VISIBLE
            }
            val mediaView = adView.mediaView
            if (isShowMedia) {
                mediaView.setListener(getMediaViewListener())
                mediaView.visibility = View.VISIBLE
            }

            val clickableViews = mutableListOf(mediaView, ctaButton)

            nativeAd.registerViewForInteraction(adView.root, mediaView, clickableViews)

            NativeComponentTag.tagView(adView.adTitle, NativeComponentTag.AD_TITLE)
            NativeComponentTag.tagView(adView.adDescription, NativeComponentTag.AD_BODY)
            NativeComponentTag.tagView(adView.callToAction, NativeComponentTag.AD_CALL_TO_ACTION)

        } catch (e: Exception) {
            e.printStackTrace()
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
        binding.btnNo.setOnClickListener { dialog.dismiss() }
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
        if (!isNativeAdActive) return
        frameLayout.addShimmer(isBig)
        val fbNative = NativeAd(context, nativeId)
        fbNative.loadAd(
            fbNative.buildLoadAdConfig()
                .withAdListener(object : NativeAdListener {
                    override fun onAdLoaded(ad: Ad?) {
                        if (ad != fbNative) return
                        if (isBig) {
                            setBigNativeAd(frameLayout, fbNative, true)
                        } else {
                            showSmallNativeAd(frameLayout, fbNative, true)
                        }
                    }

                    override fun onError(ad: Ad?, adError: AdError) {
                        Log.d(TAG, "FB Native onError: ${adError.errorMessage}")
                    }

                    override fun onAdClicked(ad: Ad?) {}
                    override fun onLoggingImpression(ad: Ad?) {}
                    override fun onMediaDownloaded(ad: Ad?) {}
                })
                .build()
        )
    }

    private fun getMediaViewListener(): MediaViewListener {
        return object : MediaViewListener {
            override fun onVolumeChange(mediaView: MediaView, volume: Float) {
                Log.i(TAG, "MediaViewEvent: Volume $volume")
            }

            override fun onPause(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: Paused")
            }

            override fun onPlay(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: Play")
            }

            override fun onFullscreenBackground(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: FullscreenBackground")
            }

            override fun onFullscreenForeground(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: FullscreenForeground")
            }

            override fun onExitFullscreen(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: ExitFullscreen")
            }

            override fun onEnterFullscreen(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: EnterFullscreen")
            }

            override fun onComplete(mediaView: MediaView) {
                Log.i(TAG, "MediaViewEvent: Completed")
            }
        }
    }

    fun getNativeAd() = nativeAd

    fun isNativeAdInitialized(): Boolean {
        val headline: String? = nativeAd?.adHeadline
        return !headline.isNullOrEmpty()
    }

    companion object {
        private const val TAG = "FB_NATIVE"

        fun FrameLayout.addShimmer(isBig: Boolean) {
            val inflater = LayoutInflater.from(this.context)
            val shimmerView = if (isBig) {
                ShimmerBigBinding.inflate(inflater)
            } else {
                ShimmerSmallBinding.inflate(inflater)
            }
            removeAllViews()
            addView(shimmerView.root)
        }
    }
}

