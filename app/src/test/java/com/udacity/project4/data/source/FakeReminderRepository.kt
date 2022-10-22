package com.udacity.project4.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import kotlinx.coroutines.runBlocking

class FakeReminderRepository: ReminderRepositoryDataSource {
    private var remindersServiceData = LinkedHashMap<Long,ReminderData>()
    private var observableReminders = MutableLiveData<Result<List<ReminderData>>>()
    private var shouldReturnError: Boolean = false


    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun getReminders(): LiveData<Result<List<ReminderData>>> {
        if(shouldReturnError) {
            observableReminders.value = Result.Error("Something went wrong")
        } else {
            observableReminders.value = Result.Success(remindersServiceData.values.toList())
        }
        return observableReminders
    }

    override suspend fun addReminder(reminder: ReminderData): Result<Long> {
        remindersServiceData[reminder.reminder_id] = reminder
        getReminders()
        return Result.Success(reminder.reminder_id)

    }

    override suspend fun getReminder(id: Long): Result<ReminderData> {
        getReminders()
        return try {
            Result.Success(remindersServiceData[id]!!)
        } catch(ex: Exception) {
            Result.Error("can not retrieve this data ${ex.message}")
        }
    }

    override suspend fun deleteReminder(id: Long) {
        remindersServiceData.remove(id)
        getReminders()
    }

    override suspend fun clearReminders() {
        remindersServiceData.clear()
        getReminders()

    }

    fun addReminders(vararg reminders: ReminderData) {
        for (reminder in reminders) {
            remindersServiceData[reminder.reminder_id] = reminder
        }
        runBlocking { getReminders() }
    }
}