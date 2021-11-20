package com.woodblockwithoutco.appsalvager.internal.exception

import com.woodblockwithoutco.appsalvager.AppSalvager

/**
 * Exception handler that records cyclic startup exceptions and instructs salvaging API
 * to put the app into salvage mode.
 *
 * After handling the exception, will pass the exception to [delegatedHandler].
 */
internal class ExceptionRecordingUncaughtExceptionHandler(
    /**
     * Handler that will receive the exception after [ExceptionRecordingUncaughtExceptionHandler]
     * is done recording it.
     *
     * Normally, it should be Android default uncaught exception handler.
     */
    private val delegatedHandler: Thread.UncaughtExceptionHandler?
): Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        AppSalvager.onUncaughtException(e, t)
        delegatedHandler?.uncaughtException(t, e)
    }
}