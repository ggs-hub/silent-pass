package com.islate.silentpass.audio

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

class RingerModeController(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(AudioManager::class.java)
    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)

    fun ringIfSilent(): Boolean {
        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) return false
        if (!notificationManager.isNotificationPolicyAccessGranted) return false

        return runCatching {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            true
        }.getOrDefault(false)
    }
}
