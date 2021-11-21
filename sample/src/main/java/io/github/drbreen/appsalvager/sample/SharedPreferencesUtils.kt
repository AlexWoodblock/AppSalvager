package io.github.drbreen.appsalvager.sample

import android.content.Context

private const val PREFS_NAME = "crash_prefs"
private const val SHOULD_CRASH_CONTENT_PROVIDER_KEY = "crash_content_provider"
private const val SHOULD_CRASH_APPLICATION_KEY = "crash_application"

fun Context.setApplicationBroken(broken: Boolean) {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(SHOULD_CRASH_APPLICATION_KEY, broken)
        .apply()
}

fun Context.setContentProviderBroken(broken: Boolean) {
    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(SHOULD_CRASH_CONTENT_PROVIDER_KEY, broken)
        .apply()
}

fun Context.isApplicationBroken(): Boolean {
    return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(SHOULD_CRASH_APPLICATION_KEY, false)
}

fun Context.isContentProviderBroken(): Boolean {
    return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(SHOULD_CRASH_CONTENT_PROVIDER_KEY, false)
}