package evgeniy.ryzhikov.callstatistics.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.os.Build
import android.os.IBinder
import android.provider.CallLog
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import evgeniy.ryzhikov.callstatistics.App
import evgeniy.ryzhikov.callstatistics.R
import evgeniy.ryzhikov.callstatistics.data.MainRepository
import evgeniy.ryzhikov.callstatistics.data.entity.PhoneTalk
import evgeniy.ryzhikov.callstatistics.ui.MainActivity
import evgeniy.ryzhikov.callstatistics.utils.convertMillisToDataTimeISO8601
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateDBService : Service() {

    private var lastUpdatedPercent = 0

    private lateinit var notificationManager: NotificationManager
    private val notificationBuilder by lazy {
        createNotificationBuilder()
    }

    @Inject lateinit var mainRepository: MainRepository
    private val updateScope = CoroutineScope(Dispatchers.IO)

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_DESCRIPTION,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notificationBuilder.build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )

        val cursor = getCursor()
        if (cursor != null) {
            updateScope.launch {
                var countPhoneTalk = mainRepository.getCountPhoneTalk().toInt()

                if (cursor.moveToLast()) {
                    do {
//                        val id = cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls._ID)) ?: ""
                        val date = cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls.DATE)) ?: 0
                        var number = cursor.getStringOrNull(cursor.getColumnIndex(CallLog.Calls.NUMBER)) ?: ""
                        val name = cursor.getStringOrNull(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) ?: ""
                        val duration = cursor.getLongOrNull(cursor.getColumnIndex(CallLog.Calls.DURATION)) ?: 0
                        val type = cursor.getIntOrNull(cursor.getColumnIndex(CallLog.Calls.TYPE)) ?: 0

                        number = if (number != "" && number[0].toString() == "8") "+7${number.substring(1)}" else number

                        val phoneTalk = PhoneTalk(
                            phoneNumber = number,
                            contactName = name,
                            type = type,
                            duration = duration,
                            dateTime = convertMillisToDataTimeISO8601(date)
                        )

                        if (isExistPhoneTalk(phoneTalk)) {
                            break
                        } else {
                            savePhoneTalkAndPhoneNumber(phoneTalk)
                        }
                        countPhoneTalk++
                        val progress = (1.0 * countPhoneTalk / cursor.count * 100).toInt()
                        updateProgress(progress)
                    } while (cursor.moveToPrevious())
                }
                if (!cursor.isClosed) {
                    cursor.close()
                }
                updateProgress(PROGRESS_MAX)
                stopSelf()
            }
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun getCursor(): Cursor? {
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.DATE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE
        )
        val where = ""

        return contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            where,
            null,
            null
        )
    }

    private fun isExistPhoneTalk(phoneTalk: PhoneTalk) = mainRepository.isExistPhoneTalk(phoneTalk)

    private fun savePhoneTalkAndPhoneNumber(phoneTalk: PhoneTalk) {
        val phoneNumber = getPhoneNumber(phoneTalk.phoneNumber)
        mainRepository.insertPhoneNumber(phoneNumber.addPhoneTalk(phoneTalk))
        mainRepository.insertPhoneTalk(phoneTalk)
    }

    private fun getPhoneNumber(number: String) = mainRepository.getPhoneNumber(number)

    private suspend fun updateProgress(progress: Int) {
        if (progress != lastUpdatedPercent) {
            lastUpdatedPercent = progress
            updateNotification(progress)
            withContext(Dispatchers.Main) {
                UpdateProgressLiveData.updateProgress(progress)
            }
        }
    }

    private fun updateNotification(progress: Int) {
        notificationBuilder
            .setContentText("$progress%")
            .setProgress(PROGRESS_MAX, progress, false)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.update_database))
            .setContentText("0%")
            .setSmallIcon(R.drawable.icon_notification)
            .setContentIntent(pendingIntent)
            .setProgress(PROGRESS_MAX, 0, true)
            .setOngoing(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_UPDATE_DB"
        const val CHANNEL_DESCRIPTION = "Channel for update Database in app CallStatistic"
        const val NOTIFICATION_ID = 1
        const val PROGRESS_MAX = 100
    }
}