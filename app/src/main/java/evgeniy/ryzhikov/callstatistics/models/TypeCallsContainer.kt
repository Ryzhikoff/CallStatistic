package evgeniy.ryzhikov.callstatistics.models

import android.os.Parcelable
import evgeniy.ryzhikov.callstatistics.utils.TypeCall
import kotlinx.parcelize.Parcelize

@Parcelize
class TypeCallsContainer(
    @TypeCall val typeCalls: Int
): Parcelable