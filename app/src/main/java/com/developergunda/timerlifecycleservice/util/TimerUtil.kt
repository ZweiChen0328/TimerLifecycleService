package com.developergunda.timerlifecycleservice.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimerUtil {
    fun getFormattedTime(timeInMillis: Long, includeMillis: Boolean): String {
        var milliseconds = timeInMillis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }

    fun getFormattedDay(timeInMillis: Long): String {
        var date: Date = Date(timeInMillis)
        var format: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.ENGLISH)
        return format.format(date)
    }

    fun decodeFormattedTime(timeInMillis: String, includeMillis: Boolean): Long {
        if (!includeMillis) {

        } else {

        }
        return 0
    }
}