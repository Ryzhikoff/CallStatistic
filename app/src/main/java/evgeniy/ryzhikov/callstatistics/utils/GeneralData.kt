package evgeniy.ryzhikov.callstatistics.utils

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneNumber

data class GeneralData(
    var totalNumbers: Int = 0,

    var totalTalks: Int = 0,
    var incoming: Int = 0,
    var outgoing: Int = 0,
    var missed: Int = 0,
    var voicemail: Int = 0,
    var rejected: Int = 0,
    var blocked: Int = 0,
    var answeredExternally: Int = 0,

    var duration: Long = 0,
    var durationInc: Long = 0,
    var durationOut: Long = 0
) {
    companion object {
        fun fill(generalData: GeneralData, phoneNumbers: List<PhoneNumber>): GeneralData {
            phoneNumbers.forEach { phoneNumber ->
                generalData.apply {
                    totalNumbers += 1
                    with(phoneNumber) {
                        totalTalks += counterIncoming + counterOutgoing + counterMissed + counterVoicemail + counterRejected + counterBlocked + counterAnsweredExternally
                        incoming += counterIncoming
                        outgoing += counterOutgoing
                        missed += counterMissed
                        voicemail += counterVoicemail
                        rejected += counterRejected
                        blocked += counterBlocked
                        answeredExternally += counterAnsweredExternally
                        duration += durationTotal
                        durationInc += durationIncoming
                        durationOut += durationOutgoing
                    }
                }
            }
            return generalData
        }
    }
}