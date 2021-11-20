package com.woodblockwithoutco.appsalvager.internal

import android.content.Context
import android.os.Handler
import com.woodblockwithoutco.appsalvager.policy.SalvageModePolicy
import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescriptionsRepository

/**
 * Holder for various components.
 */
internal class AppSalvagerComponents(
    /**
     * Context of the app.
     *
     * Assumptions about whether it's going to be ContextImpl or Application class
     * should not be made.
     */
    val context: Context,

    /**
     * Handler with main thread looper backing it.
     */
    val mainHandler: Handler,

    uptimeThreshold: Long,

    /**
     * Timestamp of app start that is going to be used for app uptime calculations.
     */
    val appStartTimestamp: Long,

    /**
     * Policy for activating salvage mode.
     */
    val salvageModePolicy: SalvageModePolicy
) {

    /**
     * Instance of [ExceptionDescriptionsRepository]. Lazily initialized.
     */
    val exceptionDescriptionsRepository: ExceptionDescriptionsRepository by lazy {
        ExceptionDescriptionsRepository(context, uptimeThreshold)
    }
}