package com.vitalforge.watch.presentation.screen.haptic

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

object HapticPatterns {
    val Alert = longArrayOf(0, 100, 50, 100)  // vibrate, pause, vibrate
    val Success = longArrayOf(0, 50, 50, 50, 50, 50)  // short pulses

    fun play(context: Context, pattern: LongArray) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }
}
