package com.islate.silentpass.ui

import android.Manifest
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islate.silentpass.contacts.ContactReader
import com.islate.silentpass.data.ContactRingOption
import com.islate.silentpass.data.ContactRingStore
import com.islate.silentpass.permissions.hasCallScreeningRole
import com.islate.silentpass.permissions.hasPermission
import com.islate.silentpass.ui.components.ContactsCard
import com.islate.silentpass.ui.components.PermissionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactRingApp() {
    val context = LocalContext.current
    val store = remember { ContactRingStore(context) }
    val contactReader = remember { ContactReader(context) }
    val roleManager = remember { context.getSystemService(RoleManager::class.java) }
    val notificationManager = remember { context.getSystemService(NotificationManager::class.java) }
    var contacts by remember { mutableStateOf(emptyList<ContactRingOption>()) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var hasContactsPermission by remember {
        mutableStateOf(context.hasPermission(Manifest.permission.READ_CONTACTS))
    }
    var hasScreeningRole by remember { mutableStateOf(context.hasCallScreeningRole(roleManager)) }
    var hasPolicyAccess by remember { mutableStateOf(notificationManager.isNotificationPolicyAccessGranted) }

    fun refresh() {
        hasContactsPermission = context.hasPermission(Manifest.permission.READ_CONTACTS)
        hasScreeningRole = context.hasCallScreeningRole(roleManager)
        hasPolicyAccess = notificationManager.isNotificationPolicyAccessGranted
        contacts = store.getSelectedContacts()
        hasUnsavedChanges = false
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refresh() }

    val roleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refresh() }

    val policyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refresh() }

    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val selectedContact = contactReader.readSelectedContact(uri)
        if (selectedContact != null) {
            contacts = (contacts.filterNot { it.lookupUri == selectedContact.lookupUri } + selectedContact)
                .sortedBy { it.displayName }
            hasUnsavedChanges = true
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Silent Pass",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(1.dp)) }
            item {
                PermissionCard(
                    hasContactsPermission = hasContactsPermission,
                    hasScreeningRole = hasScreeningRole,
                    hasPolicyAccess = hasPolicyAccess,
                    onRequestContacts = {
                        contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    },
                    onRequestRole = {
                        val intent = roleManager?.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                        if (intent != null) roleLauncher.launch(intent)
                    },
                    onRequestPolicy = {
                        policyLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                    }
                )
            }
            item {
                ContactsCard(
                    contacts = contacts,
                    hasContactsPermission = hasContactsPermission,
                    enabledCount = contacts.count { it.enabled },
                    selectedCount = contacts.size,
                    hasUnsavedChanges = hasUnsavedChanges,
                    canSave = hasUnsavedChanges,
                    onSelectContact = {
                        if (hasContactsPermission) {
                            contactPicker.launch(null)
                        } else {
                            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    },
                    onEnabledChanged = { contact, enabled ->
                        contacts = contacts.map {
                            if (it.lookupUri == contact.lookupUri) {
                                it.copy(enabled = enabled)
                            } else {
                                it
                            }
                        }
                        hasUnsavedChanges = true
                    },
                    onRemove = { contact ->
                        contacts = contacts.filterNot { it.lookupUri == contact.lookupUri }
                        hasUnsavedChanges = true
                    },
                    onSave = {
                        store.saveSnapshot(contacts)
                        hasUnsavedChanges = false
                    }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
