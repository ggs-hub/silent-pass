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
            .commit()  // 改用 commit() 替代 apply()，确保同步写入
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
            
            // 移除各种分隔符和空格
            var stripped = PhoneNumberUtils.stripSeparators(number)
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "")
                .replace("[", "")
                .replace("]", "")
            
            // 处理国家代码统一化：+86、86、0086 都转换为 86
            if (stripped.startsWith("+")) {
                stripped = stripped.substring(1)
            } else if (stripped.startsWith("00")) {
                stripped = stripped.substring(2)
            }
            
            // 过滤保留数字
            val result = stripped.filter { it.isDigit() }.takeIf { it.isNotBlank() }
                ?: return null
            
            // 验证号码长度（中国号码至少10位，国际格式至少6位）
            return result.takeIf { it.length >= 6 }
        }

        @Suppress("DEPRECATION")
        fun numbersMatch(first: String, second: String): Boolean {
            // 完全相等
            if (first == second) return true
            
            // 使用系统 API 进行智能匹配
            if (PhoneNumberUtils.compare(first, second)) return true
            
            // 后11位匹配（中国号码完整长度）
            if (first.length >= 11 && second.length >= 11) {
                if (first.takeLast(11) == second.takeLast(11)) return true
            }
            
            // 后10位匹配（中国号码不含0的完整长度）
            if (first.length >= 10 && second.length >= 10) {
                if (first.takeLast(10) == second.takeLast(10)) return true
            }
            
            return false
        }
    }
}
