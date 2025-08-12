package com.aospinsight.securesms.ui.conversation_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aospinsight.securesms.R
import com.aospinsight.securesms.model.SmsConversation
import com.aospinsight.securesms.model.SmsMessage
import com.aospinsight.securesms.model.SmsType
import com.aospinsight.securesms.repository.FakeSmsRepository
import com.aospinsight.securesms.ui.theme.SecureSmsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    viewModel: SmsConversationViewModel = viewModel(),
    onConversationClick: (SmsConversation) -> Unit,
    onStartNewChatClick: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // App Icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app's icon
                            contentDescription = "App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SecureSms",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle search action */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { /* Handle account action */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onStartNewChatClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Start new chat"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
                uiState.error != null -> {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
                uiState.conversations.isEmpty() -> {
                    Text(text = "No conversations found.", style = MaterialTheme.typography.bodyLarge)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(uiState.conversations) { conversation ->
                            ConversationListItem(
                                conversation = conversation,
                                onConversationClick = onConversationClick
                            )
                            // A more visually appealing divider
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationListScreenPreview() {
    SecureSmsTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(5) {
                val sampleMessage = SmsMessage(
                    id = it.toLong(),
                    phoneNumber = "+123456789$it",
                    message = "این یک پیام تستی است. شماره $it",
                    timestamp = System.currentTimeMillis(),
                    type = SmsType.INBOX
                )
                val sampleConversation = SmsConversation(
                    phoneNumber = "+123456789$it",
                    displayName = "مخاطب $it",
                    messages = listOf(sampleMessage),
                    lastMessageTimestamp = sampleMessage.timestamp,
                    unreadCount = it
                )
                ConversationListItem(conversation = sampleConversation) {}
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}