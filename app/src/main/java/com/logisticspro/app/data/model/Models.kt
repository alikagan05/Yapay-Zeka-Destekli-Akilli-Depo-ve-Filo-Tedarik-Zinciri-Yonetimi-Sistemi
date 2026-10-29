package com.logisticspro.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeliveryStatus {
    PENDING, ON_THE_WAY, DELIVERED, CANCELLED
}

@Serializable
data class InventoryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val shelfLocation: String
)

@Serializable
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val name: String = "",
    val isDelivered: Boolean = false
)

@Serializable
data class DeliveryTask(
    val id: Int,
    val destination: String,
    val status: DeliveryStatus,
    val items: List<InventoryItem>,
    val route: List<RoutePoint>? = null
)

@Serializable
data class StockUpdateRequest(
    val change: Int
)

@Serializable
data class StatusUpdateRequest(
    val status: DeliveryStatus
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val role: String // e.g., "WAREHOUSE_STAFF", "FLEET_DRIVER"
)

// ===================== AI CHAT MODELS =====================

@Serializable
data class AiChatRequest(
    val message: String
)

@Serializable
data class AiChatResponse(
    @SerialName("user_message") val userMessage: String,
    @SerialName("ai_response") val aiResponse: String
)

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
