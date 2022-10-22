package com.udacity.project4.data.repositories

import androidx.lifecycle.LiveData
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.adapters.Result


interface ReminderDataSource {
    //fun addGeoFence()
    fun getReminders(): LiveData<Result<List<ReminderData>>>
    suspend fun addReminder(reminder: ReminderData): Result<Long>
    suspend fun getReminder(id: Long): Result<ReminderData>
    suspend fun deleteReminder(id: Long)
    suspend fun clearReminders()
}