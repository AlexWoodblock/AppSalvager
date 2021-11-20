package com.woodblockwithoutco.appsalvager.policy

import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescription

/**
 * [SalvageModePolicy] that activates salvage mode if same exception occurs again.
 *
 * "Sameness" is defined by comparing messages, exception class names and threads where exception occurred.
 */
class SameExceptionPolicy(
    /**
     * Configuration for exception comparison.
     * By default the strictest configuration is set, meaning class, message and thread are taken into consideration.
     */
    private val configuration: Configuration = Configuration()
): SalvageModePolicy {

    override fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean {
        return if (exceptions.size >= 2) {
            exceptions[0].equalsTo(exceptions[1])
        } else {
            false
        }
    }

    private fun ExceptionDescription.equalsTo(
        exceptionDescription: ExceptionDescription
    ): Boolean {
        val classEqual = exceptionDescription.clazz == clazz
        val messageEquals = !configuration.compareMessage || exceptionDescription.message == message
        val threadEquals = !configuration.compareThread || exceptionDescription.thread == thread

        return classEqual && messageEquals && threadEquals
    }
    /**
     * Configuration that allows fine-tuning exception comparison for [SameExceptionPolicy].
     */
    data class Configuration(

        /**
         * Whether thread name should be taken into consideration when comparing
         * exceptions.
         */
        val compareThread: Boolean = true,

        /**
         * Whether exception message should be taken into consideration when comparing
         * exceptions.
         */
        val compareMessage: Boolean = true
    )

}