package com.saurabharora.customtabs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.saurabharora.customtabs.internal.CustomTabsHelper
import com.saurabharora.customtabs.internal.ServiceConnection
import com.saurabharora.customtabs.internal.ServiceConnectionCallback

class CustomTabActivityHelper2(
    private val context: Context,
    lifecycle: Lifecycle,
    connectionCallback: ConnectionCallback? = null,
    private val callback: CustomTabsCallback? = null
) : ServiceConnectionCallback, DefaultLifecycleObserver {

    private var customTabsSession: CustomTabsSession? = null
    private var client: CustomTabsClient? = null
    private var connection: CustomTabsServiceConnection? = null
    private var connectionCallback: ConnectionCallback? = null

    val session: CustomTabsSession?
        get() {
            if (client == null) {
                customTabsSession = null
            } else if (customTabsSession == null) {
                customTabsSession = client!!.newSession(callback)
            }
            return customTabsSession
        }

    init {
        this.connectionCallback = connectionCallback
        lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        bindCustomTabsService()
    }

    override fun onPause(owner: LifecycleOwner) {
        unbindCustomTabsService()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        removeReferences()
    }

    private fun unbindCustomTabsService() {
        connection?.let {
            context.unbindService(it)
            client = null
            customTabsSession = null
            connection = null
        }
    }

    private fun bindCustomTabsService() {
        if (client != null) return

        val packageName = CustomTabsHelper.getPackageNameToUse(context) ?: return

        connection = ServiceConnection(this)
        connection?.let {
            CustomTabsClient.bindCustomTabsService(context, packageName, it)
        }
    }

    private fun removeReferences() {
        connectionCallback = null
    }

    override fun onServiceConnected(client: CustomTabsClient) {
        this.client = client
        this.client?.warmup(0L)
        connectionCallback?.onCustomTabsConnected()
    }

    override fun onServiceDisconnected() {
        client = null
        customTabsSession = null
        connectionCallback?.onCustomTabsDisconnected()
    }

    fun mayLaunchUrl(
        uri: Uri,
        extras: Bundle? = null,
        otherLikelyBundles: List<Bundle>? = null
    ): Boolean {
        if (client == null) return false
        val session = session ?: return false
        return session.mayLaunchUrl(uri, extras, otherLikelyBundles)
    }
}
