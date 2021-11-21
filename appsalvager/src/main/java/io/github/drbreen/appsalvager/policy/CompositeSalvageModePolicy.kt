package io.github.drbreen.appsalvager.policy

import io.github.drbreen.appsalvager.internal.storage.ExceptionDescription

/**
 * Salvage mode policy that uses several policies to determine
 * whether salvage mode should be activated or not.
 */
class CompositeSalvageModePolicy(
    /**
     * List of policies that will be queried for salvage mode activation.
     *
     * To trigger the activation, all of them should return {@code true}.
     */
    private val policies: List<SalvageModePolicy>
): SalvageModePolicy {

    override fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean {
        return policies.all { it.isInSalvageMode(exceptions) }
    }
}