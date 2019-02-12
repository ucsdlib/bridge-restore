package org.chronopolis.bridge

import org.chronopolis.bridge.models.RestoreComplete
import org.chronopolis.bridge.models.RestoreId
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface Bridge {

    @POST("restore/{restoreId}/complete")
    fun completeRestoreById(@Path("restoreId") restoreId: RestoreId): Call<RestoreComplete>

}