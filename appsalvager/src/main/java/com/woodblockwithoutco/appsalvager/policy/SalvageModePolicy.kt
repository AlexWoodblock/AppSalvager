package com.woodblockwithoutco.appsalvager.policy

import com.woodblockwithoutco.appsalvager.internal.storage.ExceptionDescription

/**
 * Policy determining if we're in salvage mode based on provided exceptions list.
 */
interface SalvageModePolicy {

    /**
     * Returns true if salvage mode should be activated.
     */
    fun isInSalvageMode(exceptions: List<ExceptionDescription>): Boolean

}