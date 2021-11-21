package io.github.drbreen.appsalvager.internal.policy

import io.github.drbreen.appsalvager.internal.storage.ExceptionDescription
import io.github.drbreen.appsalvager.policy.SalvageModePolicy

/**
 * Policy that cuts off old exceptions and then delegates it to actual policy.
 */
internal class RecentHistoryCutoffPolicy(
    private val delegatedPolicy: SalvageModePolicy,
    private val recencyMs: Long
): SalvageModePolicy {

    override fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean {
        val timeNow = System.currentTimeMillis()

        return delegatedPolicy.isInSalvageMode(exceptions.filter {
            timeNow - it.timestamp <= recencyMs
        })
    }
}