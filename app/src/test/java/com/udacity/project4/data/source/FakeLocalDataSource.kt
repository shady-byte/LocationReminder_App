package com.udacity.project4.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderDataSource

class FakeLocalDataSource(var dataList: MutableList<ReminderData>?= mutableListOf()): ReminderDataSource {

    override fun getReminders(): LiveData<Result<List<ReminderData>>> {
        return MutableLiveData(Result.Success(dataList!!))
        //MutableLiveData<List<ReminderData>>(dataList))
    }

    override suspend fun addReminder(reminder: ReminderData): Result<Long> {
        dataList?.add(reminder)
        return Result.Success(reminder.reminder_id)
    }

    override suspend fun getReminder(id: Long): Result<ReminderData> {
        return Result.Success(dataList?.find { it -> it.reminder_id == id }!!)
    }

    override suspend fun deleteReminder(id: Long) {
        dataList?.find { it -> it.reminder_id == id }.let {
            dataList?.remove(it!!)
        }
    }

    override suspend fun clearReminders() {
        dataList?.clear()
    }
}