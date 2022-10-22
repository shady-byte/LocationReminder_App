package com.udacity.project4.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.udacity.project4.adapters.FireBaseUserLiveData
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import com.udacity.project4.ui.GEOFENCE_RADIUS_IN_METERS
import com.udacity.project4.utils.GeoFencingBroadCast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class MainActivityViewModel(private val repository: ReminderRepositoryDataSource,
                            private val context: Application): AndroidViewModel(context) {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val geofencingClient = LocationServices.getGeofencingClient(context)
    val geofenceList = mutableListOf<Geofence>()
    private val _remindersList = repository.getReminders()
    val remindersList: LiveData<Result<List<ReminderData>>>
            get() = _remindersList

    val geofenceIntent: PendingIntent by lazy {
        val intent = Intent(context, GeoFencingBroadCast::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    val authenticationState = Transformations.map(FireBaseUserLiveData()) { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "reminder location"
                enableLights(true)
                enableVibration(true)
                lightColor = Color.BLUE
            }
            val notificationManger = context.getSystemService(NotificationManager::class.java)
            notificationManger.createNotificationChannel(notificationChannel)
        }
    }

    fun geoList(item: ReminderData) {
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(item.reminder_id.toString())
                .setCircularRegion(item.latitude!!, item.longitude!!, GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        )
    }

    private fun seekGeofencing(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            addGeofences(geofenceList)
        }.build()
    }

    @SuppressLint("MissingPermission")
    fun addGeofence() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.removeGeofences(geofenceIntent).run {
            addOnCompleteListener {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                geofencingClient.addGeofences(seekGeofencing(), geofenceIntent).run {
                    addOnSuccessListener {
                        Log.d("MainTest", "Geofences is added")
                    }
                    addOnFailureListener {
                        if ((it.message != null)) {
                            Log.e("MainTest", "Geofences error ${it.message})")
                        }
                    }
                }
            }
        }
    }

}