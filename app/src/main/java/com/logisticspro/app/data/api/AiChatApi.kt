package com.logisticspro.app.data.api

import com.logisticspro.app.data.model.AiChatRequest
import com.logisticspro.app.data.model.AiChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AiChatApi {
    @POST("api/ai/chat/")
    suspend fun sendMessage(@Body request: AiChatRequest): AiChatResponse
}
