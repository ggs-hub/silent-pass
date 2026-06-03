package com.islate.silentpass.data

import android.content.Context
import android.content.SharedPreferences
import android.telephony.PhoneNumberUtils

class ContactRingStore(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getSelectedContacts(): List<ContactRingOption> =
        prefs.getStringSet(KEY_SELECTED_CONTACTS, emptySet())
            .orEmpty()
            .mapNotNull(::decodeContact)
            .sortedBy { it.displayName }

    fun saveSnapshot(contacts: List<ContactRingOption>) {
        val enabledContacts = contacts.filter { it.enabled }
        prefs.edit()
            .putStringSet(KEY_SELECTED_CONTACTS, contacts.map(::encodeContact).toSet())
            .putStringSet(KEY_ENABLED_LOOKUP_URIS, enabledContacts.map { it.lookupUri }.toSet())
            .putStringSet(KEY_ENABLED_NUMBERS, enabledContacts.flatMap { it.numbers }.toSet())
            .apply()
    }

    fun shouldRingForNumber(rawNumber: String?): Boolean {
        val incoming = normalizeNumber(rawNumber) ?: return false
        return getSavedSnapshot().enabledNumbers.any { number ->
            numbersMatch(incoming, number)
        }
    }

    fun getSavedSnapshot(): SavedRingSnapshot =
        SavedRingSnapshot(
            enabledLookupUris = prefs.getStringSet(KEY_ENABLED_LOOKUP_URIS, emptySet()).orEmpty(),
            enabledNumbers = prefs.getStringSet(KEY_ENABLED_NUMBERS, emptySet()).orEmpty()
        )

    private fun encodeContact(contact: ContactRingOption): String =
        listOf(
            contact.contactId.toString(),
            contact.lookupKey,
            contact.lookupUri,
            contact.displayName,
            contact.numbers.joinToString(NUMBER_SEPARATOR),
            contact.enabled.toString()
        ).joinToString(FIELD_SEPARATOR)

    private fun decodeContact(value: String): ContactRingOption? {
        val parts = value.split(FIELD_SEPARATOR)
        if (parts.size != 6) return null
        val contactId = parts[0].toLongOrNull() ?: return null
        val numbers = parts[4]
            .split(NUMBER_SEPARATOR)
            .mapNotNull(::normalizeNumber)
            .distinct()
        if (numbers.isEmpty()) return null

        return ContactRingOption(
            contactId = contactId,
            lookupKey = parts[1],
            lookupUri = parts[2],
            displayName = parts[3],
            numbers = numbers,
            enabled = parts[5].toBoolean()
        )
    }

    companion object {
        private const val PREFS_NAME = "contact_ring_settings"
        private const val KEY_SELECTED_CONTACTS = "selected_contacts"
        private const val KEY_ENABLED_LOOKUP_URIS = "enabled_lookup_uris"
        private const val KEY_ENABLED_NUMBERS = "enabled_numbers"
        private const val FIELD_SEPARATOR = "\u001F"
        private const val NUMBER_SEPARATOR = "\u001E"

        fun normalizeNumber(rawNumber: String?): String? {
            val number = rawNumber
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: return null
            val stripped = PhoneNumberUtils.stripSeparators(number)
                .replace(" ", "")
                .replace("-", "")
            return stripped.filterIndexed { index, char ->
                char.isDigit() || (index == 0 && char == '+')
            }.takeIf { it.isNotBlank() }
        }

        @Suppress("DEPRECATION")
        fun numbersMatch(first: String, second: String): Boolean {
            if (first == second) return true
            return PhoneNumberUtils.compare(first, second) ||
                first.takeLast(11) == second.takeLast(11) ||
                first.takeLast(8) == second.takeLast(8)
        }
    }
}
