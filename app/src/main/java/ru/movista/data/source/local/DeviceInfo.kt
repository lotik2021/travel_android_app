package ru.movista.data.source.local

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings

class DeviceInfo(private val context: Context) {

    val deviceModel: String
        get() = Build.MODEL

    // todo переделать на lazy
    val deviceId: String
        @SuppressLint("HardwareIds")
        get() {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
}