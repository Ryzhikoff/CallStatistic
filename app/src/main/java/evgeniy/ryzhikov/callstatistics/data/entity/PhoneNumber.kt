package evgeniy.ryzhikov.callstatistics.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import evgeniy.ryzhikov.callstatistics.utils.ANSWERED_EXTERNALLY_TYPE
import evgeniy.ryzhikov.callstatistics.utils.BLOCKED_TYPE
import evgeniy.ryzhikov.callstatistics.utils.INCOMING_TYPE
import evgeniy.ryzhikov.callstatistics.utils.MISSED_TYPE
import evgeniy.ryzhikov.callstatistics.utils.OUTGOING_TYPE
import evgeniy.ryzhikov.callstatistics.utils.REJECTED_TYPE
import evgeniy.ryzhikov.callstatistics.utils.TABLE_NAME_PHONE_NUMBER
import evgeniy.ryzhikov.callstatistics.utils.VOICEMAIL_TYPE

@Entity(
    tableName = TABLE_NAME_PHONE_NUMBER,
    indices = [Index(
        value = ["phone_number"],
        unique = true
    )]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "phone_number") var phoneNumber: String = "",
    @ColumnInfo(name = "contact_name") var contactName: String = "",
    @ColumnInfo(name = "duration_total") var durationTotal: Long = 0,
    @ColumnInfo(name = "duration_incoming") var durationIncoming: Long = 0,
    @ColumnInfo(name = "duration_outgoing") var durationOutgoing: Long = 0,
    @ColumnInfo(name = "counter_total") var counterTotal: Int = 0,
    @ColumnInfo(name = "counter_incoming") var counterIncoming: Int = 0,
    @ColumnInfo(name = "counter_outgoing") var counterOutgoing: Int = 0,
    @ColumnInfo(name = "counter_missed") var counterMissed: Int = 0,
    @ColumnInfo(name = "counter_voicemail") var counterVoicemail: Int = 0,
    @ColumnInfo(name = "counter_rejected") var counterRejected: Int = 0,
    @ColumnInfo(name = "counter_blocked") var counterBlocked: Int = 0,
    @ColumnInfo(name = "counter_answered_externally") var counterAnsweredExternally: Int = 0
) : PhoneData {
    fun addPhoneTalk(phoneTalk: PhoneTalk): PhoneNumber {

        if (phoneNumber == "") phoneNumber = phoneTalk.phoneNumber
        if (contactName == "") contactName = phoneTalk.contactName

        counterTotal += 1
        durationTotal += phoneTalk.duration

        when (phoneTalk.type) {
            INCOMING_TYPE -> {
                counterIncoming += 1
                durationIncoming += phoneTalk.duration
            }

            OUTGOING_TYPE -> {
                counterOutgoing += 1
                durationOutgoing += phoneTalk.duration
            }

            MISSED_TYPE -> counterMissed += 1
            VOICEMAIL_TYPE -> counterVoicemail += 1
            REJECTED_TYPE -> counterRejected += 1
            BLOCKED_TYPE -> counterBlocked += 1
            ANSWERED_EXTERNALLY_TYPE -> counterAnsweredExternally += 1
        }
        return this
    }

    companion object {
        fun addPhoneTalk(phoneNumber: PhoneNumber?, phoneTalk: PhoneTalk): PhoneNumber {
            val number = phoneNumber ?: PhoneNumber()

            return number.also { itPhoneNumber ->
                if (itPhoneNumber.phoneNumber == "") itPhoneNumber.phoneNumber =
                    phoneTalk.phoneNumber
                if (itPhoneNumber.contactName == "") itPhoneNumber.contactName =
                    phoneTalk.contactName

                itPhoneNumber.counterTotal += 1
                itPhoneNumber.durationTotal += phoneTalk.duration

                when (phoneTalk.type) {
                    INCOMING_TYPE -> {
                        itPhoneNumber.counterIncoming += 1
                        itPhoneNumber.durationIncoming += phoneTalk.duration
                    }

                    OUTGOING_TYPE -> {
                        itPhoneNumber.counterOutgoing += 1
                        itPhoneNumber.durationOutgoing += phoneTalk.duration
                    }

                    MISSED_TYPE -> itPhoneNumber.counterMissed += 1
                    VOICEMAIL_TYPE -> itPhoneNumber.counterVoicemail += 1
                    REJECTED_TYPE -> itPhoneNumber.counterRejected += 1
                    BLOCKED_TYPE -> itPhoneNumber.counterBlocked += 1
                    ANSWERED_EXTERNALLY_TYPE -> itPhoneNumber.counterAnsweredExternally += 1
                }
            }
        }
    }
}

