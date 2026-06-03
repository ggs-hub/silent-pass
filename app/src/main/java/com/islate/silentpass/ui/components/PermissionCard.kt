package com.islate.silentpass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islate.silentpass.ui.theme.CompactButtonPadding
import com.islate.silentpass.ui.theme.SoftBlue
import com.islate.silentpass.ui.theme.SoftWarning
import com.islate.silentpass.ui.theme.Warning

@Composable
fun PermissionCard(
    hasContactsPermission: Boolean,
    hasScreeningRole: Boolean,
    hasPolicyAccess: Boolean,
    onRequestContacts: () -> Unit,
    onRequestRole: () -> Unit,
    onRequestPolicy: () -> Unit
) {
    if (hasContactsPermission && hasScreeningRole && hasPolicyAccess) {
        ReadinessCard(
            hasContactsPermission = hasContactsPermission,
            hasScreeningRole = hasScreeningRole,
            hasPolicyAccess = hasPolicyAccess
        )
    } else {
        SurfaceCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "权限状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                PermissionActionRow(
                    label = "联系人",
                    description = "读取所选联系人信息",
                    enabled = hasContactsPermission,
                    actionLabel = "去授权",
                    onAction = onRequestContacts
                )
                PermissionActionRow(
                    label = "来电检测",
                    description = "识别来电号码",
                    enabled = hasScreeningRole,
                    actionLabel = "去设置",
                    onAction = onRequestRole
                )
                PermissionActionRow(
                    label = "静音响铃",
                    description = "在静音模式下允许响铃",
                    enabled = hasPolicyAccess,
                    actionLabel = "去开启",
                    onAction = onRequestPolicy
                )
            }
        }
    }
}

@Composable
private fun ReadinessCard(
    hasContactsPermission: Boolean,
    hasScreeningRole: Boolean,
    hasPolicyAccess: Boolean
) {
    SurfaceCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                StatusBadge(
                    text = "✓",
                    background = SoftBlue,
                    foreground = MaterialTheme.colorScheme.primary,
                    size = 36.dp
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "权限已就绪",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "联系人、来电检测、静音响铃均已启用",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = listOf(hasContactsPermission, hasScreeningRole, hasPolicyAccess).count { it }.toString() + "/3",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PermissionActionRow(
    label: String,
    description: String,
    enabled: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(
                text = if (enabled) "✓" else "!",
                background = if (enabled) SoftBlue else SoftWarning,
                foreground = if (enabled) MaterialTheme.colorScheme.primary else Warning,
                size = 38.dp
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (enabled) {
            OutlinedButton(
                onClick = {},
                enabled = false,
                shape = RoundedCornerShape(8.dp),
                contentPadding = CompactButtonPadding,
                modifier = Modifier.defaultMinSize(minWidth = 60.dp, minHeight = 34.dp)
            ) {
                Text("已启用")
            }
        } else {
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(8.dp),
                contentPadding = CompactButtonPadding,
                modifier = Modifier.defaultMinSize(minWidth = 60.dp, minHeight = 34.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}
