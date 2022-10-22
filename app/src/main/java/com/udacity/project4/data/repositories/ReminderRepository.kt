package com.udacity.project4.data.repositories

import androidx.lifecycle.LiveData
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.adapters.Result
import com.udacity.project4.utils.wrapEspressoIdlingResource
import java.lang.Exception

class ReminderRepository(private val dataSource: ReminderDataSource) :
    ReminderRepositoryDataSource {

    override fun getReminders(): LiveData<Result<List<ReminderData>>> {
        wrapEspressoIdlingResource {
            return dataSource.getReminders()
        }

    }

    override suspend fun addReminder(reminder: ReminderData): Result<Long> {
        wrapEspressoIdlingResource {
            return try {
                dataSource.addReminder(reminder)
            } catch (ex: Exception) {
                Result.Error("error during adding a reminder")
            }
        }

    }

    override suspend fun getReminder(id: Long): Result<ReminderData> {
        wrapEspressoIdlingResource {
            return try {
                dataSource.getReminder(id)
            } catch (ex: Exception) {
                Result.Error("error during getting a reminder")
            }
        }
    }

    override suspend fun deleteReminder(id: Long) {
        wrapEspressoIdlingResource {
            dataSource.deleteReminder(id)
        }
    }

    override suspend fun clearReminders() {
        wrapEspressoIdlingResource {
            dataSource.clearReminders()//database.clearDataBase()
        }
    }
}