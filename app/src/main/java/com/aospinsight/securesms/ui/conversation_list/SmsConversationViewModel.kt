package com.aospinsight.securesms.ui.conversation_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aospinsight.securesms.model.SmsConversation
import com.aospinsight.securesms.repository.SmsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the list of SMS conversations.
 *
 * This ViewModel handles the UI state, including the list of conversations,
 * loading status, and any potential errors. It fetches data from the
 * SmsRepository and exposes it as StateFlows for the UI to observe.
 *
 * @param smsRepository The repository dependency, injected via the constructor.
 */
class SmsConversationViewModel(private val smsRepository: SmsRepository) : ViewModel() {

    // --- Private MutableStateFlows for internal state management ---
    // We can use the StateFlows directly from the repository
    // No need to create new ones here.

    /**
     * Combines the conversations and loading states from the repository into a single StateFlow
     * for easier UI consumption.
     */
    val uiState: StateFlow<SmsConversationsUiState> =
        combine(smsRepository.conversations, smsRepository.isLoading, smsRepository.error) {
                conversations, isLoading, error ->
            SmsConversationsUiState(
                conversations = conversations,
                isLoading = isLoading,
                error = error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SmsConversationsUiState()
        )

    /**
     * A simple data class to hold the UI state.
     * This is a modern approach for managing complex UI states.
     */
    data class SmsConversationsUiState(
        val conversations: List<SmsConversation> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    init {
        // Load conversations when the ViewModel is first created
        loadConversations()
    }

    /**
     * Loads the SMS conversations from the repository.
     * This coroutine is launched within the viewModelScope, ensuring it's
     * cancelled automatically when the ViewModel is cleared.
     */
    fun loadConversations() {
        viewModelScope.launch {
            smsRepository.loadConversations()
        }
    }
}