package evgeniy.ryzhikov.callstatistics.utils

import evgeniy.ryzhikov.callstatistics.data.entity.PhoneData

interface ClickListener {
    fun onClick(phoneData: PhoneData)
}