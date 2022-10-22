package com.udacity.project4.viewModels

import androidx.lifecycle.*
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import kotlinx.coroutines.launch

const val LOCATION_PERMISSION_INDEX = 0
const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

class RemindersListViewModel(private val repository: ReminderRepositoryDataSource): ViewModel() {
    val remindersList: LiveData<Result<List<ReminderData>>> = repository.getReminders()
    val error: LiveData<Boolean> = remindersList.map { it is Result.Error }

    fun clearReminders() {
        viewModelScope.launch {
            repository.clearReminders()
        }
    }

}