package com.vitalforge.watch.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vitalforge.watch.util.WorkManagerHelper

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            WorkManagerHelper.scheduleAll(context)
        }
    }
}
