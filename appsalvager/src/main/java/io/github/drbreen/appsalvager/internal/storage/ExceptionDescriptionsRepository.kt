package io.github.drbreen.appsalvager.internal.storage

import android.content.Context
import io.github.drbreen.appsalvager.internal.log.debugLog
import io.github.drbreen.appsalvager.internal.log.errorLog
import io.github.drbreen.appsalvager.internal.log.verboseLog
import io.github.drbreen.appsalvager.storage.ExceptionDescription
import java.io.File
import kotlin.Exception

/**
 * Repository providing access to stored exception descriptions.
 */
internal class ExceptionDescriptionsRepository(
    private val context: Context,
    private val uptimeThreshold: Long
) {

    /**
     * Clear the repository.
     */
    fun clear() {
        val exceptionsFile = File(context.filesDir, EXCEPTIONS_FILENAME)
        if (exceptionsFile.exists()) {
            exceptionsFile.delete()
        }
    }

    /**
     * Store new exception description.
     */
    fun addException(description: ExceptionDescription) {
        if (description.uptimeTimestamp > uptimeThreshold) {
            debugLog(TAG, "Ignoring exception because it is not related to app start anymore")
            return
        }

        try {
            val exceptionsFile = File(context.filesDir, EXCEPTIONS_FILENAME)
            val existingLines = if (exceptionsFile.exists()) {
                exceptionsFile.readLines()
            } else {
                emptyList()
            }

            val fullySerialized = (existingLines + description.serialize())
                .joinToString("\n")

            exceptionsFile.writeText(fullySerialized)
        } catch (e: Exception) {
            errorLog(TAG, "Failed to add exception", e)
        }
    }

    /**
     * Load stored descriptions.
     */
    fun load(): List<ExceptionDescription> {
        return try {
            File(context.filesDir, EXCEPTIONS_FILENAME)
                .takeIf {
                    it.exists()
                }
                ?.readLines()
                ?.filter {
                    it.isNotEmpty()
                }
                ?.mapNotNull {
                    ExceptionDescription.fromSerialized(it)
                }
                ?.filter {
                    it.uptimeTimestamp <= uptimeThreshold
                } ?: emptyList()
        } catch (e: Exception) {
            errorLog(TAG, "Failed to load descriptions. Returning empty list.", e)
            emptyList()
        }
    }

    private companion object {
        private const val EXCEPTIONS_FILENAME = "salvage.exceptions"
        private const val TAG = "AppSalvage.repo"
    }

}