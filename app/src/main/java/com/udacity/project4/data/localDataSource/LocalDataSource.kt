package com.udacity.project4.data.localDataSource

import androidx.lifecycle.LiveData
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.repositories.ReminderDataSource
import androidx.lifecycle.map

class LocalDataSource(private val database: RemindersDao): ReminderDataSource {

    override fun getReminders(): LiveData<Result<List<ReminderData>>> {
        return database.getAllReminders().map {
            try {
                Result.Success(it)
            }catch (ex: Exception) {
                Result.Error("can not retrieve data now ${ex.message}")
            }
        }
    }

    override suspend fun addReminder(reminder: ReminderData): Result<Long> {
        return try {
            Result.Success(database.createReminder(reminder))
        }catch (ex: Exception) {
            Result.Error("error during adding the Reminder $ex")
        }
    }

    override suspend fun getReminder(id: Long): Result<ReminderData> {
        return try {
            Result.Success(database.getReminderWithId(id))
        }catch (ex: Exception) {
            Result.Error("error with getting a reminder $ex")
        }
    }

    override suspend fun deleteReminder(id: Long) {
        database.deleteReminder(id)
    }

    override suspend fun clearReminders() {
        database.clearDataBase()
    }
}