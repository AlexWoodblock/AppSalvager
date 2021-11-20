package com.woodblockwithoutco.appsalvager.policy

import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescription

/**
 * Policy determining if we're in salvage mode based on provided exceptions list.
 */
interface SalvageModePolicy {

    /**
     * Calculate whether salvage mode should be triggered based on given exception history.
     *
     * @param exceptions List of recent exceptions. Ordering is from the newest to the oldest.
     * Non-recent exceptions will be automatically filtered out, so there's no need to filter for them
     * in your implementation.
     *
     * @return {@code true} if salvage mode should be triggered now, {@code false} otherwise.
     */
    fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean

}