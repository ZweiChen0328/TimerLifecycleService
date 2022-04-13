package com.developergunda.timerlifecycleservice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.developergunda.timerlifecycleservice.*
import com.developergunda.timerlifecycleservice.model.TimerEvent
import com.developergunda.timerlifecycleservice.util.TimerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class TimerService : LifecycleService() {
    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
        val totalTime = MutableLiveData<Long>()
    }

    private var isServiceStopped = false
    private val usageMap = mutableMapOf<String, Long>()
    private var notifyState = 0
    private val allowList = listOf(
        "com.developergunda.timerlifecycleservice",
        "com.google.android.apps.nexuslauncher"
    )

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    //Timer properties
    private var lapTime = 0L
    private var timeStarted = 0L
    private var timeEnded = 0L

    override fun onCreate() {
        super.onCreate()
        initValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    Timber.d("start service")
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("stop service")
                    stopService()
                }
                ACTION_GIVEUP_SERVICE -> {
                    Timber.d("give up service")
                    giveUpService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initValues() {
        timerEvent.postValue(TimerEvent.END)
        timerInMillis.postValue(0L)
    }

    private fun giveUp() {
        timerEvent.postValue(TimerEvent.GIVEUP)
        timerInMillis.postValue(0L)
    }

    private fun startForegroundService() {
        timerEvent.postValue(TimerEvent.START)
        startTimer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        timerInMillis.observe(this, Observer {
            if (!isServiceStopped && notifyState == 0) {
                notificationBuilder.setContentText(
                    TimerUtil.getFormattedTime(it, false)
                )
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            } else if (!isServiceStopped && notifyState == 1) {
                notificationBuilder.setContentText("使用其他app超過5秒")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            } else if (!isServiceStopped && notifyState == 2) {
                notificationBuilder.setContentText("終止計時")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                Timber.d("give up service")
                giveUpService()
            }
        })
    }

    private fun stopService() {
        isServiceStopped = true
        initValues()
        cancelService()
        stopSelf()
    }

    private fun giveUpService() {
        isServiceStopped = true
        giveUp()
        cancelService()
        stopSelf()
    }

    private fun cancelService() {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        stopForeground(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)
    }


    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        timeEnded = System.currentTimeMillis() + totalTime.value!!
        CoroutineScope(Dispatchers.Main).launch {
            while (timerEvent.value!! == TimerEvent.START && !isServiceStopped) {
                lapTime = timeEnded - System.currentTimeMillis()
                if (lapTime <= 0) {
                    Timber.d("stop service")
                    stopService()
                    break
                }
                detectApp()
                timerInMillis.postValue(lapTime)
                delay(50L)
            }
        }
    }

    private fun detectApp() {
        val timeUsageStats: UsageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val dataUsageStats: List<UsageStats> = timeUsageStats.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            timeStarted,
            System.currentTimeMillis()
        )

//        var stats_data: String = ""
        for (i in 0 until dataUsageStats.toSet().size) {
            if (System.currentTimeMillis() - dataUsageStats[i].lastTimeUsed <= System.currentTimeMillis() - timeStarted) {
                usageMap[dataUsageStats[i].packageName] = dataUsageStats[i].lastTimeUsed
                if (usageMap[dataUsageStats[i].packageName]!! <= System.currentTimeMillis() - 10 * 1000 && !allowList.contains(
                        dataUsageStats[i].packageName
                    )
                ) {
                    if (usageMap[dataUsageStats[i].packageName] == dataUsageStats[i].lastTimeUsed) {
                        notifyState = 2
                    } else {
                        Timber.d("非使用中")
                    }
                } else if (usageMap[dataUsageStats[i].packageName]!! <= System.currentTimeMillis() - 5 * 1000 && !allowList.contains(
                        dataUsageStats[i].packageName
                    )
                ) {
                    if (usageMap[dataUsageStats[i].packageName] == dataUsageStats[i].lastTimeUsed) {
                        notifyState = 1
                    } else {
                        Timber.d("非使用中")
                    }
                }
            }
        }
    }
}