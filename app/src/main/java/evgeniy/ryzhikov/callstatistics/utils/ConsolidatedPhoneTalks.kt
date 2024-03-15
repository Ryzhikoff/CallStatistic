package evgeniy.ryzhikov.callstatistics.utils

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk

class ConsolidatedPhoneTalks(phoneTalks: List<PhoneTalk>, val dateTime: String = "") {
    var countIncoming = 0
    var countOutgoing = 0
    var durationIncoming = 0L
    var durationOuting = 0L

    var totalAverageDuration: Long = 0
    var incomingAverageDuration: Long = 0
    var outgoingAverageDuration: Long = 0

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
        val totalCount = countIncoming + countOutgoing
        val totalDuration = durationIncoming + durationOuting
        if (totalCount != 0)  totalAverageDuration = totalDuration / totalCount
        if (countIncoming != 0) incomingAverageDuration = durationIncoming / countIncoming
        if (countOutgoing != 0) outgoingAverageDuration = durationOuting / countOutgoing
    }
}