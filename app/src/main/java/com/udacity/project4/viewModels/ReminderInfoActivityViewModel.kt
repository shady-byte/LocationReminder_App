package com.udacity.project4.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.project4.data.localDataSource.ReminderData

class ReminderInfoActivityViewModel: ViewModel() {
    var reminderTitle = MutableLiveData<String>()
    var reminderDescription = MutableLiveData<String>()
    var reminderLocation = MutableLiveData<String>()

    fun displayReminder(reminder: ReminderData) {
        reminderTitle.value = reminder.title
        reminderDescription.value = reminder.description!!
        reminderLocation.value = reminder.location!!
    }

}
