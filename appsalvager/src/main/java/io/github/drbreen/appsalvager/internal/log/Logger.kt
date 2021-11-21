package io.github.drbreen.appsalvager.internal.log

import android.util.Log
import io.github.drbreen.appsalvager.BuildConfig

private const val BASE_TAG = "AppSalvager"

/**
 * Log at error level.
 */
internal fun errorLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.e(BASE_TAG, "[$tag] $message", throwable)
    }
}


/**
 * Log at warning level.
 */
internal fun warningLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.w(BASE_TAG, "[$tag] $message", throwable)
    }
}


/**
 * Log at informational level.
 */
internal fun infoLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.i(BASE_TAG, "[$tag] $message", throwable)
    }
}

/**
 * Log at debug level.
 */
internal fun debugLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.d(BASE_TAG, "[$tag] $message", throwable)
    }
}

/**
 * Log at verbose level.
 */
internal fun verboseLog(tag: String, message: String, throwable: Throwable? = null) {
    if (BuildConfig.LOGS_ENABLED) {
        Log.w(BASE_TAG, "[$tag] $message", throwable)
    }
}
