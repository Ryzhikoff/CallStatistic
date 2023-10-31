package evgeniy.ryzhikov.callstatistics.utils

import android.text.format.DateFormat
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.R
import java.util.Calendar

fun convertDuration(duration: Long, isSeparated: Boolean = false, separator: String = " ") : String {
    var dur = duration
    var result = ""

    val day = dur / (60 * 60 * 24)
    dur = if (day > 0) {
        result += "$day${App.instance.resources.getString(R.string.day_short)}"
        if (isSeparated) result += separator
        dur - day * (60 * 60 * 24)
    } else dur

    val hour = dur / (60 * 60)
    dur = if (hour > 0) {
        result += "$hour${App.instance.resources.getString(R.string.hour_short)}"
        if (isSeparated) result += separator
        dur - hour * (60 * 60)
    } else dur

    val minute = dur / 60
    dur = if (minute > 0) {
        result += "$minute${App.instance.getString(R.string.minute_short)}"
        if (isSeparated) result += separator
        dur - minute * 60
    } else dur

    val second = dur
    result += "$second${App.instance.getString(R.string.second_short)}"

    return result
}

fun convertTypePhoneTalkToString(@TypeCall type: Int) : String {
    val context = App.instance
    return when (type) {
        INCOMING_TYPE -> context.resources.getString(R.string.incoming)
        OUTGOING_TYPE -> context.resources.getString(R.string.outgoing)
        MISSED_TYPE -> context.resources.getString(R.string.missed)
        VOICEMAIL_TYPE -> context.resources.getString(R.string.voice_email)
        REJECTED_TYPE -> context.resources.getString(R.string.rejected)
        BLOCKED_TYPE -> context.resources.getString(R.string.blocked)
        ANSWERED_EXTERNALLY_TYPE -> context.resources.getString(R.string.answered_externally)
        else -> context.resources.getString(R.string.unknown)
    }
}

fun getDateFromLong(millis: Long): String {
    val calendar = Calendar.Builder()
        .setInstant(millis)
        .build()
    return DateFormat.format("dd.MM.yyyy", calendar.time).toString()
}

fun getTimeFromLong(millis: Long): String {
    val calendar = Calendar.Builder()
        .setInstant(millis)
        .build()
    return DateFormat.format("HH:mm:ss", calendar.time).toString()
}

fun getDataTimeISO8601(millis: Long): String{
    val date = Calendar.Builder()
        .setInstant(millis)
        .build()
    return DateFormat.format("yyyy-MM-dd'T'HH:mm:ss", date.time).toString()
}