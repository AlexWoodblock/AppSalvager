package com.woodblockwithoutco.appsalvager.internal.log

import android.util.Log
import com.woodblockwithoutco.appsalvager.BuildConfig

private const val BASE_TAG = "AppSalvager"

/**
 * Log at error level.
 */
fun errorLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.e(BASE_TAG, "[$tag] $message", throwable)
    }
}


/**
 * Log at warning level.
 */
fun warningLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.w(BASE_TAG, "[$tag] $message", throwable)
    }
}


/**
 * Log at informational level.
 */
fun infoLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.i(BASE_TAG, "[$tag] $message", throwable)
    }
}

/**
 * Log at debug level.
 */
fun debugLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.d(BASE_TAG, "[$tag] $message", throwable)
    }
}

/**
 * Log at verbose level.
 */
fun verboseLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.w(BASE_TAG, "[$tag] $message", throwable)
    }
}
