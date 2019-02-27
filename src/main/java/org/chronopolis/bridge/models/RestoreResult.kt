package org.chronopolis.bridge.models

import org.chronopolis.bridge.RestoreTuple

/**
 * Various results which we expect to see when trying to stage data for Duracloud
 *
 * Note that we could make this generic, however given the scope of this application there's no
 * reason to.
 *
 * @since 1.0
 * @author shake
 */
sealed class RestoreResult {
    abstract fun message(): String

    data class Success(val data: RestoreTuple) : RestoreResult() {
        override fun message(): String {
            val restore = data.restore
            return "Restoration ${restore.restorationId} was successfully staged for upload " +
                    "to duracloud"
        }
    }

    data class Error(val data: RestoreTuple,
                     val details: String) : RestoreResult() {
        override fun message(): String {
            val restore = data.restore
            return "Restoration ${restore.restorationId} was unable to be completed: \n  $details"
        }
    }

    data class ErrorException(val data: RestoreTuple,
                              val details: String,
                              val exception: Exception) : RestoreResult() {
        override fun message(): String {
            val restore = data.restore
            return "Restoration ${restore.restorationId} was unable to be completed: \n  $details" +
                    "${exception.stackTrace} "
        }
    }
}
