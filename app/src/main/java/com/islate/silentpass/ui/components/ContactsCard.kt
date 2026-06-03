package com.islate.silentpass.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islate.silentpass.data.ContactRingOption
import com.islate.silentpass.ui.theme.CompactButtonPadding
import com.islate.silentpass.ui.theme.SoftBlue
import com.islate.silentpass.ui.theme.SoftWarning
import com.islate.silentpass.ui.theme.Warning

@Composable
fun ContactsCard(
    contacts: List<ContactRingOption>,
    hasContactsPermission: Boolean,
    enabledCount: Int,
    selectedCount: Int,
    hasUnsavedChanges: Boolean,
    canSave: Boolean,
    onSelectContact: () -> Unit,
    onEnabledChanged: (ContactRingOption, Boolean) -> Unit,
    onRemove: (ContactRingOption) -> Unit,
    onSave: () -> Unit
) {
    SurfaceCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "联系人",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "选择后保存，来电时按快照响铃",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(
                    onClick = onSelectContact,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = CompactButtonPadding,
                    modifier = Modifier.defaultMinSize(minWidth = 52.dp, minHeight = 34.dp)
                ) {
                    Text("选择")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            when {
                contacts.isEmpty() && !hasContactsPermission -> EmptyText(
                    text = "授权联系人权限后，可以手动选择需要临时响铃的人。",
                    modifier = Modifier.padding(16.dp)
                )
                contacts.isEmpty() -> EmptyText(
                    text = "还没有选择联系人。",
                    modifier = Modifier.padding(16.dp)
                )
                else -> ContactListContent(
                    contacts = contacts,
                    onEnabledChanged = onEnabledChanged,
                    onRemove = onRemove
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SaveCard(
                enabledCount = enabledCount,
                selectedCount = selectedCount,
                hasUnsavedChanges = hasUnsavedChanges,
                canSave = canSave,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun SaveCard(
    enabledCount: Int,
    selectedCount: Int,
    hasUnsavedChanges: Boolean,
    canSave: Boolean,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (hasUnsavedChanges) SoftWarning else Color(0xFFFFFBF4))
            .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(
                text = if (hasUnsavedChanges) "!" else "✓",
                background = if (hasUnsavedChanges) Color(0xFFFFE8BD) else SoftBlue,
                foreground = if (hasUnsavedChanges) Warning else MaterialTheme.colorScheme.primary,
                size = 36.dp
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = if (hasUnsavedChanges) "有未保存更改" else "设置已保存",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "已选 $selectedCount 人，开启 $enabledCount 人",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Button(
            onClick = onSave,
            enabled = canSave,
            shape = RoundedCornerShape(8.dp),
            contentPadding = CompactButtonPadding,
            modifier = Modifier.defaultMinSize(minWidth = 68.dp, minHeight = 34.dp)
        ) {
            Text("保存设置")
        }
    }
}

@Composable
private fun ContactListContent(
    contacts: List<ContactRingOption>,
    onEnabledChanged: (ContactRingOption, Boolean) -> Unit,
    onRemove: (ContactRingOption) -> Unit
) {
    Column {
        contacts.forEachIndexed { index, contact ->
            ContactRow(
                contact = contact,
                onEnabledChanged = { enabled -> onEnabledChanged(contact, enabled) },
                onRemove = { onRemove(contact) }
            )
            if (index != contacts.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 66.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: ContactRingOption,
    onEnabledChanged: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(contact.displayName)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = contact.numbers.joinToString(" / "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = onRemove,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
                modifier = Modifier.defaultMinSize(minWidth = 42.dp, minHeight = 34.dp)
            ) {
                Text("删除")
            }
            Spacer(Modifier.width(4.dp))
            Switch(
                checked = contact.enabled,
                onCheckedChange = onEnabledChanged,
                modifier = Modifier.scale(0.88f)
            )
        }
    }
}
