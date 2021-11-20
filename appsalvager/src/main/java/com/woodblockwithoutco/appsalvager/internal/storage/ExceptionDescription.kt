package com.woodblockwithoutco.appsalvager.internal.storage

import android.util.Base64
import kotlin.Exception

/**
 * Exception description that is stored on the disk.
 */
data class ExceptionDescription(
    /**
     * Class name of the exception.
     */
    val clazz: String,

    /**
     * Thread name.
     */
    val thread: String,

    /**
     * Message of the exception.
     */
    val message: String,

    /**
     * Milliseconds that passed from app start when this exception occurred.
     */
    val uptimeTimestamp: Long
) {

    /**
     * Serialize description into custom format.
     */
    internal fun serialize(): String {
        return clazz.toBase64() +
                "," + thread.toBase64() +
                "," + message.toBase64() +
                "," + uptimeTimestamp
    }

    internal companion object {

        /**
         * Convert exception to its' description.
         */
        internal fun Throwable.toDescription(thread: Thread, uptimeTimestamp: Long): ExceptionDescription {
            return ExceptionDescription(
                clazz = javaClass.name,
                thread = thread.name,
                message = message ?: "-",
                uptimeTimestamp = uptimeTimestamp
            )
        }

        /**
         * Deserialize exception description from a string.
         */
        internal fun fromSerialized(str: String): ExceptionDescription? {
            return try {
                val parts = str.split(",")

                ExceptionDescription(
                    clazz = parts[0].fromBase64(),
                    thread = parts[1].fromBase64(),
                    message = parts[2].fromBase64(),
                    uptimeTimestamp = parts[3].toLong()
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
    }
}