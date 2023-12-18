package evgeniy.ryzhikov.callstatistics.ui.rv

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData
import evgeniy.ryzhikov.callstatistics.utils.ClickListener


data class TopItem(
    val contactName: String,
    val phoneNumber: String,
    val value: String,
    val phoneData: PhoneData,
    val clickListener: ClickListener
) : TopItemInterface
