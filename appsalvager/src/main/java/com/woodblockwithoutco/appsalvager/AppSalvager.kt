package com.woodblockwithoutco.appsalvager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.woodblockwithoutco.appsalvager.internal.AppSalvagerComponents
import com.woodblockwithoutco.appsalvager.internal.exception.ExceptionRecordingUncaughtExceptionHandler
import com.woodblockwithoutco.appsalvager.internal.SalvageActivity
import com.woodblockwithoutco.appsalvager.internal.log.infoLog
import com.woodblockwithoutco.appsalvager.internal.util.processName
import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescription.Companion.toDescription
import com.woodblockwithoutco.appsalvager.policy.SalvageModePolicy

/**
 * Entry point for application salvaging API.
 */
// TODO: better and more complete docs for everything.
// TODO: write README.md
object AppSalvager {

    // yeah, this context is going to be saved anyway
    @SuppressLint("StaticFieldLeak")
    private lateinit var components: AppSalvagerComponents

    private val shouldStartSalvage: Boolean
        get() {
            return !inSalvageMode && components.salvageModePolicy.isInSalvageMode(
                exceptions = components.exceptionDescriptionsRepository.load()
            )
        }

    /**
     * true when process is currently being salvaged after repeated crashes.
     */
    val inSalvageMode: Boolean
        get() {
            return processName().endsWith(SALVAGE_PROCESS_ID)
        }

    /**
     * Reset recorded exception history.
     *
     * This method should be called when we are sure that undelying issue has been fixed during salvaging mode.
     * This will also be called automatically after [AppSalvager.Config.uptimeThreshold] milliseconds.
     */
    fun resetExceptions() {
        components.exceptionDescriptionsRepository.clear()
    }

    /**
     * This method should be called when uncaught exception occurs in the app.
     *
     * In case [Config.installExceptionHandler] was set to {@code true}, you do not need to call this method - it will
     * be called automatically.
     */
    fun onUncaughtException(throwable: Throwable, thread: Thread) {
        if (inSalvageMode) {
            infoLog(TAG, "Ignoring exception because we're in salvage mode already", throwable)
            return
        }

        val uptimeTimestamp = System.currentTimeMillis() - components.appStartTimestamp
        components.exceptionDescriptionsRepository.addException(throwable.toDescription(thread, uptimeTimestamp))

        if (shouldStartSalvage) {
            startSalvage()
        }
    }

    /**
     * Install required app salvager components.
     */
    fun install(config: Config) {
        components = AppSalvagerComponents(
            context = config.context,
            appStartTimestamp = System.currentTimeMillis(),
            salvageModePolicy = config.policy,
            uptimeThreshold = config.uptimeThreshold,
            mainHandler = Handler(Looper.getMainLooper())
        )
        scheduleAutomaticExceptionsReset(config.uptimeThreshold)

        createSalvageView = config.createSalvageView

        if (config.installExceptionHandler) {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(
                ExceptionRecordingUncaughtExceptionHandler(delegatedHandler = currentHandler)
            )
        }
    }

    private fun scheduleAutomaticExceptionsReset(afterMs: Long) {
        if (!inSalvageMode) {
            components.mainHandler.postDelayed({
                infoLog(TAG, "No exception occurred in $afterMs ms after app start, resetting exceptions history")
                resetExceptions()
            }, afterMs)
        } else {
            infoLog(TAG, "Not scheduling exceptions reset - in salvage mode")
        }
    }

    private fun startSalvage() {
        infoLog(TAG, "Entering salvage mode...")

        with(components.context) {
            startActivity(
                SalvageActivity.createIntent(this)
            )
        }
    }

    internal lateinit var createSalvageView: (Activity) -> View

    /**
     * App salvaging configuration.
     */
    data class Config(

        /**
         * Context of the application.
         *
         * Typically, application object will be used for that.
         */
        val context: Context,

        /**
         * Install exception handler that will automatically record exceptions.
         *
         * In case this is {@code true}, you will have to call
         */
        val installExceptionHandler: Boolean,

        /**
         * Policy that will determine if salvage mode should be activated.
         *
         * You can use one of provided ones, or define your own.
         */
        val policy: SalvageModePolicy,

        /**
         * Factory for creating View that will be displayed on salvage
         * screen.
         */
        val createSalvageView: (Activity) -> View,

        /**
         * Defines threshold for handling exceptions. If exception
         * occurs in [uptimeThreshold] milliseconds from app start,
         * then it is regarded as application initialization exception.
         *
         * Otherwise, this exception is ignored by [AppSalvager].
         *
         * It is set to 2500 milliseconds by default
         */
        val uptimeThreshold: Long = DEFAULT_UPTIME_THRESHOLD
    )

    private const val DEFAULT_UPTIME_THRESHOLD = 2500L
    private const val SALVAGE_PROCESS_ID = "salvage"
    private const val TAG = "General"
}