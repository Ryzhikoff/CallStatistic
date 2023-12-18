package evgeniy.ryzhikov.callstatistics.ui.type_calls.rv

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData
import evgeniy.ryzhikov.callstatistics.utils.ClickListener


data class TopItem(
    val contactName: String,
    val phoneNumber: String,
    val value: String,
    val phoneData: PhoneData,
    val clickListener: ClickListener
) : TopItemInterface
