package org.chronopolis.bridge

import org.chronopolis.bridge.config.DuracloudConfig
import org.chronopolis.bridge.models.RestoreId
import org.chronopolis.bridge.models.RestoreResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Class for Notifications to a [Bridge]
 *
 * @since 1.0
 * @author shake
 */
class BridgeNotification(val config: DuracloudConfig) {
    private val log: Logger = LoggerFactory.getLogger(BridgeNotification::class.java)

    /**
     * Notify a Duracloud [Bridge] that a [RestoreTuple] has been successfully staged for return
     * into Duracloud
     *
     * @param result A successful [RestoreResult] from staging data
     * @return The updated [RestoreResult] from trying to notify the [Bridge]
     */
    fun notify(result: RestoreResult.Success): RestoreResult {
        val bridge = config.bridge()
        val restore = result.data.restore
        val errorMsg = "Error completing Bridge API call"
        val exceptionMsg = "Exception communicating with the Bridge"

        val call = bridge.completeRestoreById(RestoreId(restore.restorationId))
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                log.info("[{}] Completed Bridge Notification", restore.restorationId)
                RestoreResult.Success(result.data)
            } else {
                log.warn("[{}] $errorMsg {} {}",
                        restore.restorationId, response.code(), response.message())
                RestoreResult.Error(result.data, errorMsg + " ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            log.warn("[{}] $exceptionMsg", restore.restorationId, e)
            RestoreResult.ErrorException(result.data, exceptionMsg, e)
        }
    }
}