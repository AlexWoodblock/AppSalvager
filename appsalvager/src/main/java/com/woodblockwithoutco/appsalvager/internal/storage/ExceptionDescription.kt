package com.woodblockwithoutco.appsalvager.internal.storage

import android.util.Base64
import kotlin.Exception

/**
 * Simplified exception description to avoid error-prone reflection-based [Exception] class
 * serialization.
 */
data class ExceptionDescription(

    /**
     * Full class name of the exception.
     */
    val clazz: String,

    /**
     * Thread name.
     */
    val thread: String,

    /**
     * Message of the exception (if it's present).
     */
    val message: String?,

    /**
     * Milliseconds that passed from app start when this exception occurred.
     */
    val uptimeTimestamp: Long,

    /**
     * Timestamp when this exception occurred.
     */
    val timestamp: Long
) {

    private val serializableMessage: String
        get() = message ?: MISSING_MESSAGE

    /**
     * Serialize description into custom format.
     *
     * Uses custom format to avoid extra dependencies and potential errors.
     */
    internal fun serialize(): String {
        return clazz.toBase64() +
                "," + thread.toBase64() +
                "," + serializableMessage.toBase64() +
                "," + uptimeTimestamp +
                "," + timestamp
    }

    internal companion object {

        /**
         * Convert exception to its' description.
         */
        internal fun Throwable.toDescription(
            thread: Thread,
            uptimeTimestamp: Long,
            timestamp: Long
        ): ExceptionDescription {
            return ExceptionDescription(
                clazz = javaClass.name,
                thread = thread.name,
                message = message,
                uptimeTimestamp = uptimeTimestamp,
                timestamp = timestamp
            )
        }

        /**
         * Deserialize exception description from a string.
         *
         * Uses custom implementation to avoid extra dependencies and potential errors.
         */
        internal fun fromSerialized(str: String): ExceptionDescription? {
            return try {
                val parts = str.split(",")

                ExceptionDescription(
                    clazz = parts[0].fromBase64(),
                    thread = parts[1].fromBase64(),
                    message = parts[2].fromBase64().takeIf {
                        it != MISSING_MESSAGE
                    },
                    uptimeTimestamp = parts[3].toLong(),
                    timestamp = parts[4].toLong()
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun String.fromBase64(): String {
            return String(Base64.decode(this, 0))
        }

        private fun String.toBase64(): String {
            val byteArray = Base64.encode(toByteArray(), Base64.NO_WRAP)
            return String(byteArray)
        }

        private const val MISSING_MESSAGE = "ExceptionDescription.missingMessage"
    }
}