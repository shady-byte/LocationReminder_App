package com.udacity.project4.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import kotlinx.coroutines.launch

class SaveReminderViewModel(private val repository: ReminderRepositoryDataSource): ViewModel() {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderLocation = MutableLiveData<String>()
    val reminderLatitude = MutableLiveData<Double>()
    val reminderLongitude = MutableLiveData<Double>()
    val showLoading = MutableLiveData<Boolean>(false)

    val remindersList: LiveData<Result<List<ReminderData>>> = repository.getReminders()

    fun addReminder() {
        showLoading.value = true
        val reminder = ReminderData(reminderTitle.value.toString(),reminderDescription.value.toString(),
            reminderLocation.value.toString(),reminderLatitude.value,reminderLongitude.value)
        viewModelScope.launch {
            repository.addReminder(reminder)
            clearFields()
            showLoading.value = false
        }
    }

    fun clearFields() {
        reminderTitle.value = ""
        reminderDescription.value =""
        reminderLongitude.value = 0.0
        reminderLatitude.value = 0.0
        reminderLocation.value = ""
    }
}
