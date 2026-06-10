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

    /**
     * 从 URI 中提取电话号码
     * 处理多种 URI 格式：
     * - tel:+8610123456789
     * - tel://+8610123456789
     * - sip:+8610123456789@example.com
     */
    private fun Uri.extractPhoneNumber(): String? {
        // 首先尝试获取 schemeSpecificPart（规范的号码部分）
        val number = schemeSpecificPart
            ?.takeIf { it.isNotBlank() }
            // 如果为空，使用完整 URI 字符串并移除 scheme
            ?: toString()
                .removePrefix("tel:")
                .removePrefix("sip:")
                .takeIf { it.isNotBlank() }
        
        // 移除所有 @ 之后的内容（处理 SIP 格式）
        return number?.substringBefore("@")?.takeIf { it.isNotBlank() }
    }
}
