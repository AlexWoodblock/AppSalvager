package io.github.drbreen.appsalvager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.view.View
import io.github.drbreen.appsalvager.internal.AppSalvagerComponents
import io.github.drbreen.appsalvager.internal.exception.ExceptionRecordingUncaughtExceptionHandler
import io.github.drbreen.appsalvager.internal.SalvageActivity
import io.github.drbreen.appsalvager.internal.log.infoLog
import io.github.drbreen.appsalvager.internal.policy.RecentHistoryCutoffPolicy
import io.github.drbreen.appsalvager.internal.util.processName
import io.github.drbreen.appsalvager.storage.ExceptionDescription.Companion.toDescription
import io.github.drbreen.appsalvager.policy.SalvageModePolicy

/**
 * Entry point for application salvaging API.
 */
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
     * Returns {@code true} when process is currently being salvaged after repeated crashes.
     *
     * In this case, normal initialization process should be terminated early to avoid
     * triggering the crash again.
     */
    val inSalvageMode: Boolean
        get() {
            return processName().endsWith(SALVAGE_PROCESS_ID)
        }

    /**
     * Mark the problem that caused repeated startup exception to occur as fixed.
     *
     * This method should be called when we are sure that underlying issue has been fixed during salvaging mode.
     * This will also be called automatically after [AppSalvager.Configuration.uptimeThreshold] milliseconds.
     */
    fun onProblemFixed() {
        components.exceptionDescriptionsRepository.clear()
    }

    /**
     * This method should be called when uncaught exception occurs in the app.
     *
     * For internal use only.
     */
    internal fun onUncaughtException(throwable: Throwable, thread: Thread) {
        if (throwable is Error) {
            // yikes! We're not touching THAT!
            return
        }

        if (inSalvageMode) {
            infoLog(TAG, "Ignoring exception because we're in salvage mode already", throwable)
            return
        }

        val timestamp = System.currentTimeMillis()
        val uptimeTimestamp = timestamp - components.appStartTimestamp
        components.exceptionDescriptionsRepository.addException(throwable.toDescription(
            thread = thread,
            uptimeTimestamp = uptimeTimestamp,
            timestamp = timestamp
        ))

        if (shouldStartSalvage) {
            startSalvage()
        }
    }

    /**
     * Install required salvage mode components.
     *
     * This method should be in [Application.attachBaseContext].
     *
     * @param configuration Salvage mode configuration - what kind of view to display, what kind of policy to apply
     * to determine when we should go into salvage mode, etc.
     *
     * @param context Base context passed to [Application.attachBaseContext] or [Application] instance.
     *
     * @throws IllegalArgumentException If context is not usable because base context
     * hasn't been attached yet.
     */
    fun install(
        configuration: Configuration,
        context: Context
    ) {
        context.verifyUsable()

        components = AppSalvagerComponents(
            context = context,
            appStartTimestamp = System.currentTimeMillis(),
            salvageModePolicy = RecentHistoryCutoffPolicy(
                delegatedPolicy = configuration.policy,
                recencyMs = configuration.allowedExceptionRecency
            ),
            uptimeThreshold = configuration.uptimeThreshold,
            mainHandler = Handler(Looper.getMainLooper())
        )

        scheduleAutomaticExceptionsReset(configuration.uptimeThreshold)

        createSalvageView = configuration.createSalvageView

        val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            ExceptionRecordingUncaughtExceptionHandler(delegatedHandler = currentHandler)
        )
    }

    private fun Context.verifyUsable() {
        if (this is ContextWrapper && baseContext == null) {
            throw IllegalArgumentException(
                "Base context is not attached yet. Please call super.attachBaseContext() first"
            )
        }
    }

    private fun scheduleAutomaticExceptionsReset(afterMs: Long) {
        if (!inSalvageMode) {
            components.mainHandler.postDelayed({
                infoLog(TAG, "No exception occurred in $afterMs ms after app start, resetting exceptions history")
                onProblemFixed()
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
     * App salvage mode configuration.
     */
    data class Configuration(
        /**
         * Policy that will determine if salvage mode should be activated.
         *
         * You can use one of provided ones, or define your own by implementing [SalvageModePolicy].
         */
        val policy: SalvageModePolicy,

        /**
         * Factory for creating View that will be displayed on salvage
         * screen.
         *
         * This factory should be as slim as possible. Anything that could trigger a crash should be avoided.
         * Recommended approach is to create a view, assign content that guides user to solving repeated crashes
         * (like buttons leading to system application settings where user can delete app data).
         *
         * Interaction with other [Context]-based Android APIs are possible, but not recommended.
         *
         * If other initialization are performed in [Application.onCreate] (like Dagger graph initialization),
         * then your factory should not rely on them as they are not going to be initialized.
         */
        // TODO: also pass latest cause of the crash!
        val createSalvageView: (Activity) -> View,

        /**
         * Defines threshold for handling exceptions. If exception
         * occurs in less than [uptimeThreshold] milliseconds from app start,
         * then it is regarded as application initialization exception.
         *
         * Otherwise, this exception is ignored by [AppSalvager].
         *
         * It is set to 2500 milliseconds by default.
         */
        val uptimeThreshold: Long = DEFAULT_UPTIME_THRESHOLD,

        /**
         * How old an exception should be to exclude it from policy evaluation (in milliseconds).
         *
         * It is set to 5 minutes (300,000 ms) by default.
         */
        val allowedExceptionRecency: Long = DEFAULT_EXCEPTION_RECENCY
    )

    private const val DEFAULT_UPTIME_THRESHOLD = 2500L
    private const val DEFAULT_EXCEPTION_RECENCY = 300_000L
    private const val SALVAGE_PROCESS_ID = "salvage"
    private const val TAG = "General"
}