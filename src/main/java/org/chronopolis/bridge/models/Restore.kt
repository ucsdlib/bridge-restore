package org.chronopolis.bridge.models

inline class RestoreId(val id: String)
data class RestoreComplete(val status: BridgeStatus, val details: String)