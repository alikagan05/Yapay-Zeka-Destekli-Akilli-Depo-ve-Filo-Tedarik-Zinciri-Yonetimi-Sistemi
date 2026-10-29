package com.logisticspro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.logisticspro.app.data.api.AiChatApi
import com.logisticspro.app.data.model.AiChatRequest
import com.logisticspro.app.data.model.ChatMessage
import com.logisticspro.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiChatViewModel : ViewModel() {

    private val api: AiChatApi = RetrofitClient.create()

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                content = "Merhaba! Ben Akıllı Lojistik Asistanıyım. 🤖\n\nStok durumu, araç verimliliği, sevkiyat planlaması ve maliyet azaltma gibi konularda size yardımcı olabilirim.\n\nNasıl yardımcı olabilirim?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(content = text.trim(), isUser = true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = api.sendMessage(AiChatRequest(message = text.trim()))
                val aiMessage = ChatMessage(
                    content = response.aiResponse,
                    isUser = false
                )
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                // Backend erişilemezse mock yanıt döner
                val mockResponse = generateMockResponse(text.trim())
                val aiMessage = ChatMessage(
                    content = mockResponse,
                    isUser = false
                )
                _messages.value = _messages.value + aiMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockResponse(query: String): String {
        val lowerQuery = query.lowercase()
        return when {
            lowerQuery.contains("stok") || lowerQuery.contains("envanter") ->
                "📦 **Stok Analizi Raporu**\n\n" +
                "Mevcut depo doluluk oranı %84 seviyesinde. " +
                "3 üründe kritik stok seviyesinin altına düşülmüş:\n\n" +
                "• Palet Sarma Filmi — 12 adet (Kritik: 20)\n" +
                "• Karton Kutu (60x40) — 45 adet (Kritik: 50)\n" +
                "• Etiket Rulosu — 8 adet (Kritik: 15)\n\n" +
                "💡 Öneri: Bu ürünler için acil tedarik siparişi açmanızı öneriyorum."

            lowerQuery.contains("araç") || lowerQuery.contains("filo") || lowerQuery.contains("verimlilik") ->
                "🚚 **Filo Verimlilik Raporu**\n\n" +
                "Aktif araç sayısı: 12 / 15\n" +
                "Ortalama rota tamamlama süresi: 4.2 saat\n" +
                "En verimli araç: 34 ABC 456 — günlük 8 teslimat\n\n" +
                "⚠️ 34 XYZ 789 plakalı araç son 3 gündür bakımda. " +
                "Tahmini dönüş: yarın."

            lowerQuery.contains("teslimat") || lowerQuery.contains("sevkiyat") || lowerQuery.contains("rota") ->
                "📋 **Günlük Sevkiyat Özeti**\n\n" +
                "Toplam sevkiyat: 24\n" +
                "Tamamlanan: 18 (%75)\n" +
                "Yolda: 4\n" +
                "Bekleyen: 2\n\n" +
                "🗺️ En yoğun bölge: Kadıköy-Ataşehir hattı (8 teslimat)\n\n" +
                "💡 Öneri: Ataşehir bölgesine ek bir araç yönlendirerek teslimat süresini %20 kısaltabilirsiniz."

            lowerQuery.contains("maliyet") || lowerQuery.contains("bütçe") || lowerQuery.contains("fiyat") ->
                "💰 **Maliyet Analizi**\n\n" +
                "Bu ayki toplam operasyonel maliyet: ₺245,000\n" +
                "Yakıt gideri: ₺85,000 (%34.7)\n" +
                "Personel gideri: ₺120,000 (%49)\n" +
                "Depo gideri: ₺40,000 (%16.3)\n\n" +
                "📉 Geçen aya göre %3.2 artış.\n\n" +
                "💡 Öneri: Rota optimizasyonu ile yakıt maliyetlerini %12 düşürebilirsiniz."

            lowerQuery.contains("merhaba") || lowerQuery.contains("selam") || lowerQuery.contains("hey") ->
                "Merhaba! 👋 Size nasıl yardımcı olabilirim?\n\n" +
                "Şu konularda sorularınızı yanıtlayabilirim:\n" +
                "• 📦 Stok ve envanter durumu\n" +
                "• 🚚 Filo ve araç verimliliği\n" +
                "• 📋 Sevkiyat ve teslimat planlaması\n" +
                "• 💰 Maliyet analizi ve optimizasyon"

            else ->
                "Sorunuzu inceledim. 🔍\n\n" +
                "Bu konuda detaylı analiz yapabilmem için biraz daha bilgi verebilir misiniz?\n\n" +
                "Örneğin şu konularda doğrudan yardımcı olabilirim:\n" +
                "• \"Stok durumu nedir?\"\n" +
                "• \"En verimli araç hangisi?\"\n" +
                "• \"Bugünkü sevkiyat özeti\"\n" +
                "• \"Maliyet analizi yap\""
        }
    }
}
