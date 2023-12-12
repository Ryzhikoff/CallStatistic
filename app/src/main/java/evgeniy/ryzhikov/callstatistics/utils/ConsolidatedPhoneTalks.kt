package evgeniy.ryzhikov.callstatistics.utils

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk

class ConsolidatedPhoneTalks(val phoneTalks: List<PhoneTalk>, val dateTime: String = "") {
    var countIncoming = 0
    var countOutgoing = 0
    var durationIncoming = 0L
    var durationOuting = 0L

    init {
        phoneTalks.forEach { phoneTalk ->
            when (phoneTalk.type) {
                INCOMING_TYPE -> {
                    countIncoming++
                    durationIncoming += phoneTalk.duration
                }
                OUTGOING_TYPE -> {
                    countOutgoing++
                    durationOuting += phoneTalk.duration
                }
            }
        }
    }
}