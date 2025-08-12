package com.aospinsight.securesms.repository

import com.aospinsight.securesms.model.SmsConversation
import com.aospinsight.securesms.model.SmsMessage
import com.aospinsight.securesms.model.SmsType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * A fake repository implementation for UI previews and testing.
 * This class now implements the SmsRepository interface, ensuring full compatibility
 * with the ViewModel.
 */
class FakeSmsRepository : SmsRepository {

    // --- Dummy Data ---
    private val conversationsData: List<SmsConversation>

    // --- StateFlows to mimic repository state ---
    private val _conversations = MutableStateFlow<List<SmsConversation>>(emptyList())
    override val conversations: Flow<List<SmsConversation>> = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: Flow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: Flow<String?> = _error.asStateFlow()

    init {
        // Initialize dummy data in the init block to ensure it's ready.
        conversationsData = createFakeConversations()
    }

    private fun createFakeConversations(): List<SmsConversation> {
        val messages1 = listOf(
            SmsMessage(id = 1, phoneNumber = "+1234567890", message = "سلام، چطوری؟", timestamp = 1625097600000, type = SmsType.INBOX, isRead = true),
            SmsMessage(id = 2, phoneNumber = "+1234567890", message = "سلام، عالی! تو چطوری؟", timestamp = 1625097700000, type = SmsType.SENT, isRead = true),
            SmsMessage(id = 3, phoneNumber = "+1234567890", message = "فردا همدیگه رو ببینیم؟", timestamp = 1625097800000, type = SmsType.INBOX, isRead = false)
        )
        val messages2 = listOf(
            SmsMessage(id = 4, phoneNumber = "+9876543210", message = "یادآوری: جلسه ساعت ۱۰ صبح.", timestamp = 1625184000000, type = SmsType.INBOX, isRead = false),
            SmsMessage(id = 5, phoneNumber = "+9876543210", message = "اوکی، می‌بینمت.", timestamp = 1625184100000, type = SmsType.SENT, isRead = true)
        )
        val messages3 = listOf(
            SmsMessage(id = 6, phoneNumber = "+1122334455", message = "کد تایید شما: ۱۲۳۴۵۶", timestamp = 1625270400000, type = SmsType.INBOX, isRead = true),
            SmsMessage(id = 7, phoneNumber = "+1122334455", message = "ممنون از کد.", timestamp = 1625270500000, type = SmsType.SENT, isRead = true)
        )
        val messages4 = listOf(
            SmsMessage(id = 8, phoneNumber = "+5556667777", message = "محصول جدیدمون منتشر شد!", timestamp = 1625356800000, type = SmsType.INBOX, isRead = false)
        )
        val messages5 = listOf(
            SmsMessage(id = 9, phoneNumber = "+1112223333", message = "فاکتور #۱۰۱ هفته آینده سررسید می‌شود.", timestamp = 1625443200000, type = SmsType.INBOX, isRead = false),
            SmsMessage(id = 10, phoneNumber = "+1112223333", message = "باشه، بررسی می‌کنم.", timestamp = 1625443300000, type = SmsType.SENT, isRead = false)
        )

        return listOf(
            SmsConversation(phoneNumber = "+1234567890", displayName = "اسماعیل", messages = messages1, lastMessageTimestamp = 1625097800000, unreadCount = 1),
            SmsConversation(phoneNumber = "+9876543210", displayName = "مریم", messages = messages2, lastMessageTimestamp = 1625184100000, unreadCount = 1),
            SmsConversation(phoneNumber = "+1122334455", displayName = "بانک سامان", messages = messages3, lastMessageTimestamp = 1625270500000, unreadCount = 0),
            SmsConversation(phoneNumber = "+5556667777", displayName = "بازاریابی", messages = messages4, lastMessageTimestamp = 1625356800000, unreadCount = 1),
            SmsConversation(phoneNumber = "+1112223333", displayName = "حسابداری", messages = messages5, lastMessageTimestamp = 1625443300000, unreadCount = 2)
        )
    }

    /**
     * Simulates loading all SMS conversations with a delay.
     * This function is now fully compatible with the real SmsRepository.
     */
    override suspend fun loadConversations() {
        _isLoading.value = true
        _error.value = null
        try {
            // Simulate a network/database delay
            delay(500)
            _conversations.value = conversationsData
        } catch (e: Exception) {
            _error.value = "Failed to load fake conversations: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Retrieves messages for a specific phone number from the fake data.
     */
    override suspend fun getMessagesForPhoneNumber(phoneNumber: String): List<SmsMessage> {
        return withContext(Dispatchers.Default) {
            conversationsData.find { it.phoneNumber == phoneNumber }?.messages ?: emptyList()
        }
    }

    /**
     * Searches messages in the fake data based on a query.
     */
    override suspend fun searchMessages(query: String): List<SmsMessage> {
        return withContext(Dispatchers.Default) {
            conversationsData.flatMap { it.messages }
                .filter { it.message.contains(query, ignoreCase = true) }
        }
    }

    /**
     * Calculates the total unread count from the fake conversations.
     */
    override suspend fun getUnreadCount(): Int {
        return withContext(Dispatchers.Default) {
            conversationsData.sumOf { it.unreadCount }
        }
    }

    /**
     * Get a specific conversation from the fake data.
     */
    override suspend fun getConversation(phoneNumber: String): SmsConversation? =
        withContext(Dispatchers.Default) {
            conversationsData.find { it.phoneNumber == phoneNumber }
        }

    override fun getAllConversations(): List<SmsConversation> {
        return _conversations.value
    }

    /**
     * Refresh conversations
     */
    override suspend fun refresh() {
        loadConversations()
    }

    /**
     * Clear error state
     */
    override fun clearError() {
        _error.value = null
    }
}