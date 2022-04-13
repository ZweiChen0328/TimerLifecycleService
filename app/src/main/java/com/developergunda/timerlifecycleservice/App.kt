package com.developergunda.timerlifecycleservice

import android.app.Application
import com.developergunda.timerlifecycleservice.data.TimeRoomDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App: Application() {
    val database: TimeRoomDatabase by lazy { TimeRoomDatabase.getDatabase(this) }
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}