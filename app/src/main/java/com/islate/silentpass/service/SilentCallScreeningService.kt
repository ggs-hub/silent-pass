package com.islate.silentpass.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import com.islate.silentpass.audio.RingerModeController
import com.islate.silentpass.data.ContactRingStore

class SilentCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.extractPhoneNumber()
        val shouldRing = ContactRingStore(this).shouldRingForNumber(number)
        if (shouldRing) {
            RingerModeController(this).ringIfSilent()
        }

        respondToCall(
            callDetails,
            CallResponse.Builder()
                .setDisallowCall(false)
                .build()
        )
    }

    private fun Uri.extractPhoneNumber(): String? =
        schemeSpecificPart?.takeIf { it.isNotBlank() } ?: toString().takeIf { it.isNotBlank() }
}
