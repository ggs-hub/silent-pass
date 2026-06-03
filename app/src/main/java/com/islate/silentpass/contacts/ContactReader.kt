package com.islate.silentpass.contacts

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.islate.silentpass.data.ContactRingOption
import com.islate.silentpass.data.ContactRingStore

class ContactReader(private val context: Context) {
    fun readSelectedContact(contactUri: Uri): ContactRingOption? {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.LOOKUP_KEY
        )

        context.contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return null
            val contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val displayName = cursor.getString(
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            ).orEmpty()
            val lookupKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY))
            val lookupUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey).toString()
            val numbers = readPhoneNumbers(contactId)
            if (numbers.isEmpty()) return null

            return ContactRingOption(
                contactId = contactId,
                lookupKey = lookupKey.orEmpty(),
                lookupUri = lookupUri,
                displayName = displayName.ifBlank { numbers.first() },
                numbers = numbers,
                enabled = true
            )
        }

        return null
    }

    private fun readPhoneNumbers(contactId: Long): List<String> {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val numbers = mutableListOf<String>()

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            arrayOf(contactId.toString()),
            null
        )?.use { cursor ->
            val numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                ContactRingStore.normalizeNumber(cursor.getString(numberIndex))?.let(numbers::add)
            }
        }

        return numbers.distinct()
    }
}
