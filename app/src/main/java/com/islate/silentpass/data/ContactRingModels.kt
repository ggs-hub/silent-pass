package com.islate.silentpass.data

data class ContactRingOption(
    val contactId: Long,
    val lookupKey: String,
    val lookupUri: String,
    val displayName: String,
    val numbers: List<String>,
    val enabled: Boolean
)

data class SavedRingSnapshot(
    val enabledLookupUris: Set<String>,
    val enabledNumbers: Set<String>
)
