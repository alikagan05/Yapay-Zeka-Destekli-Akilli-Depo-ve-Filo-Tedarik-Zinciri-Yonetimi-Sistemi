package com.logisticspro.app.data.api

import com.logisticspro.app.data.model.DeliveryTask
import com.logisticspro.app.data.model.StatusUpdateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface FleetApi {
    @GET("tasks/assigned/")
    suspend fun getMyTasks(): List<DeliveryTask>

    @PATCH("tasks/{id}/")
    suspend fun updateTaskStatus(
        @Path("id") taskId: Int,
        @Body request: StatusUpdateRequest
    ): DeliveryTask
}
