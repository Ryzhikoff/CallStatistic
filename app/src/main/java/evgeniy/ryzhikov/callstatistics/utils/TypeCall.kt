package evgeniy.ryzhikov.callstatistics.utils

import androidx.annotation.IntDef

const val INCOMING_TYPE = 1

/** Call log type for outgoing calls.  */
const val OUTGOING_TYPE = 2

/** Call log type for missed calls.  */
const val MISSED_TYPE = 3

/** Call log type for voicemails.  */
const val VOICEMAIL_TYPE = 4

/** Call log type for calls rejected by direct user action.  */
const val REJECTED_TYPE = 5

/** Call log type for calls blocked automatically.  */
const val BLOCKED_TYPE = 6

/**
 * Call log type for a call which was answered on another device.  Used in situations where
 * a call rings on multiple devices simultaneously and it ended up being answered on a
 * device other than the current one.
 */
const val ANSWERED_EXTERNALLY_TYPE = 7

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(
    value = [
        INCOMING_TYPE,
        OUTGOING_TYPE,
        MISSED_TYPE,
        VOICEMAIL_TYPE,
        REJECTED_TYPE,
        BLOCKED_TYPE,
        ANSWERED_EXTERNALLY_TYPE
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class TypeCall
