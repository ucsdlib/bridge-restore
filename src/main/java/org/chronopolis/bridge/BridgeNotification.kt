package org.chronopolis.bridge

import org.chronopolis.bridge.models.RestoreId
import org.chronopolis.bridge.models.Result

/**
 * Class for Notifications to a [Bridge]
 *
 * @since 1.0
 * @author shake
 */
class BridgeNotification(val bridge: Bridge) {

    /**
     * Notify a Duracloud [Bridge] that a [RestoreTuple] has been successfully staged for return
     * into Duracloud
     *
     * @param result A successful [Result] from staging data
     * @return The updated [Result] from trying to notify the [Bridge]
     */
    fun notify(result: Result.Success): Result {
        val restore = result.data.restore
        val errorMsg = "Error completing Bridge API call"
        val exceptionMsg = "Exception communicating with the Bridge"

        val call = bridge.completeRestoreById(RestoreId(restore.restorationId))
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                Result.Success(result.data)
            } else {
                Result.Error(result.data, errorMsg + " ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Result.ErrorException(result.data, exceptionMsg, e)
        }
    }
}