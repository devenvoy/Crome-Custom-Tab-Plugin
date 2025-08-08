package com.devansh.common.core.utils

import android.content.Context
import android.net.ConnectivityManager

fun isNetworkAvailable(c: Context): Boolean {
    val manager = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = manager.activeNetworkInfo
    var isAvailable = false
    if (networkInfo != null && networkInfo.isConnected) {
        isAvailable = true
    }
    return isAvailable
}
