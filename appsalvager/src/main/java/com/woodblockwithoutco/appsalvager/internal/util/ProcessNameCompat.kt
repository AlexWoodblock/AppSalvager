package com.woodblockwithoutco.appsalvager.internal.util

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import com.woodblockwithoutco.appsalvager.internal.log.errorLog
import java.lang.Exception

/**
 * Obtain process-name in backwards-compatible way.
 */
internal fun processName(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        processNameReflectionV18()
    } else {
        processNameReflection()
    }
}

@SuppressLint("PrivateApi")
private fun processNameReflection(methodName: String): String {
    return try {
        val activityThreadClass = Class.forName(ACTIVITY_THREAD_CLASS)
        val processNameMethod = activityThreadClass.getDeclaredMethod(methodName)
        processNameMethod.invoke(null) as String
    } catch (e: Exception) {
        errorLog(TAG, "Failed to obtain process name - returning empty one", e)
        ""
    }
}

/**
 * Since API 18, there is ActivityThread.currentProcessName() method to obtain process name.
 */
private fun processNameReflectionV18() = processNameReflection(V18_REFLECTION_PROCESS_METHOD_NAME)

/**
 * Before API 18, a method to obtain process name was called ActivityThread.currentPackageName().
 *
 * Nice one.
 */
private fun processNameReflection() = processNameReflection(REFLECTION_PROCESS_METHOD_NAME)

private const val V18_REFLECTION_PROCESS_METHOD_NAME = "currentProcessName"
private const val REFLECTION_PROCESS_METHOD_NAME = "currentPackageName"

private const val TAG = "AppSalvage.processName"
private const val ACTIVITY_THREAD_CLASS = "android.app.ActivityThread"