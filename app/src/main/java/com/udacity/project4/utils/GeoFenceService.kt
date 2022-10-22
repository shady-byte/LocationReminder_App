package com.udacity.project4.utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.content.ContextCompat
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class GeoFenceService: JobIntentService() {
    companion object {
        fun onEnqueueWork(context: Context,intent: Intent) {
            enqueueWork(context, GeoFenceService::class.java,101,intent)
        }
    }
    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError()!!) {
            val errorMessage = "something went wrong with geofence service"
            Log.e("MainTest", errorMessage)
            return
        }
        handleEvent(geofencingEvent)

    }

    private fun handleEvent(event: GeofencingEvent) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        coroutine.launch {
            if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                sendNotification(event.triggeringGeofences)
                }
            }
        }

    private fun sendNotification(triggeringGeofences: List<Geofence>?) {
        val coroutine = CoroutineScope(Dispatchers.IO)
        val repository by inject<ReminderRepositoryDataSource>()
        val notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        coroutine.launch {
            for (geoFence in triggeringGeofences!!) {
                val result = repository.getReminder(geoFence.requestId.toLong())
                if (result is Result.Success<ReminderData>) {
                    val reminder = result.data
                    if (reminder != null) {
                        Log.d("ServiceTest", reminder.toString())
                        notificationManager.sendNotification(
                            reminder, applicationContext
                        )
                        repository.deleteReminder(reminder.reminder_id)
                    }
                }
                /*
            val requestId = triggeringGeofences?.get(0)!!.requestId.toLong()
            val result = repository.getReminder(requestId)
            if(result is Result.Success<ReminderData>) {
                val reminder = result.data
                if(reminder !=null) {
                    Log.d("ServiceTest",reminder.toString())
                    notificationManager.sendNotification(
                        reminder,applicationContext)
                    repository.deleteReminder(reminder.reminder_id)
                }

             */
            }
        }
    }
}
