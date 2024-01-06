package evgeniy.ryzhikov.callstatistics.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import evgeniy.ryzhikov.callstatistics.utils.TABLE_NAME_PHONE_TALK
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = TABLE_NAME_PHONE_TALK,
    indices = [Index(
        value = [
            "phone_number",
            "dateTime",
        ],
        unique = true
    )]
)
data class PhoneTalk(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "contact_name") val contactName: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "dateTime") val dateTime: String,
) : Parcelable, PhoneData


