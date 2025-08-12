package com.aospinsight.securesms.ui.conversation_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aospinsight.securesms.model.SmsConversation
import com.aospinsight.securesms.model.SmsMessage
import com.aospinsight.securesms.model.SmsType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListItem(
    conversation: SmsConversation,
    onConversationClick: (SmsConversation) -> Unit
) {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val contactName = conversation.displayName ?: conversation.phoneNumber
    val firstLetter = contactName.firstOrNull()?.uppercaseChar() ?: '?'

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConversationClick(conversation) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = firstLetter.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Conversation details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contactName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = conversation.lastMessage?.message ?: "No messages",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Timestamp and unread badge
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = formatter.format(Date(conversation.lastMessageTimestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (conversation.unreadCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = "${conversation.unreadCount}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationListItemPreview() {
    val sampleMessage = SmsMessage(
        id = 1,
        phoneNumber = "+1234567890",
        message = "سلام، چطوری؟ این یک پیام تست طولانی برای نمایش قابلیت Truncate هست.",
        timestamp = System.currentTimeMillis(),
        type = SmsType.INBOX
    )
    val sampleConversation = SmsConversation(
        phoneNumber = "+1234567890",
        displayName = "اسماعیل",
        messages = listOf(sampleMessage),
        lastMessageTimestamp = sampleMessage.timestamp,
        unreadCount = 2
    )

    MaterialTheme {
        ConversationListItem(conversation = sampleConversation) {}
    }
}