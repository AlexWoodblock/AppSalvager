package com.woodblockwithoutco.appsalvager.policy

import com.woodblockwithoutco.appsalvager.internal.log.verboseLog
import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescription

/**
 * [SalvageModePolicy] that activates based on number of exceptions.
 */
class ExceptionNumberPolicy(
    private val threshold: Int = DEFAULT_THRESHOLD
): SalvageModePolicy {

    override fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean {
        val inSalvageMode = exceptions.size >= threshold
        verboseLog(TAG, "${exceptions.size} >= $threshold = ${exceptions.size >= threshold}")
        return inSalvageMode
    }

    private companion object {
        private const val TAG = "NumPolicy"
        private const val DEFAULT_THRESHOLD = 3
    }
}