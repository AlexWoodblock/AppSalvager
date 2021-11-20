package com.woodblockwithoutco.appsalvager.internal.exception

import com.woodblockwithoutco.appsalvager.AppSalvager

/**
 * Exception handler that records cyclic startup exceptions and instructs salvaging API
 * to put the app into salvage mode.
 */
internal class ExceptionRecordingUncaughtExceptionHandler(
    private val delegatedHandler: Thread.UncaughtExceptionHandler?
): Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        AppSalvager.onUncaughtException(e, t)
        delegatedHandler?.uncaughtException(t, e)
    }
}