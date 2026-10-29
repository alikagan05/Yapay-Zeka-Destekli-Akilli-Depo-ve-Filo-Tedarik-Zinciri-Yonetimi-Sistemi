package com.logisticspro.app.data.api

import com.logisticspro.app.data.model.InventoryItem
import com.logisticspro.app.data.model.StockUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WarehouseApi {
    @GET("inventory/")
    suspend fun getInventory(): List<InventoryItem>

    @POST("inventory/{id}/update_stock/")
    suspend fun updateStock(
        @Path("id") itemId: Int,
        @Body request: StockUpdateRequest
    ): Response<Unit>
}
