package com.udacity.project4.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.ui.ReminderInfoActivityViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ReminderInfoActivityViewModelTest {
    private lateinit var reminderInfoViewModel: ReminderInfoActivityViewModel

    private val reminder1 = ReminderData("reminder 1","one task today","Abo Noura,Alexandria",3.0,5.2,54L)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createViewModel() {
        reminderInfoViewModel = ReminderInfoActivityViewModel()
    }

    @Test
    fun getReminderTitle_TheTitleOfReminderReturnedFromNotification() {
        reminderInfoViewModel.reminderTitle.value  =reminder1.title
        assertEquals(reminder1.title,reminderInfoViewModel.reminderTitle.value)
    }

    @Test
    fun getReminderDescription_TheDescriptionOfReminderReturnedFromNotification() {
        reminderInfoViewModel.reminderDescription.value  = reminder1.description
        assertEquals(reminder1.description,reminderInfoViewModel.reminderDescription.value)
    }

    @Test
    fun getReminderLocation_TheLocationOfReminderReturnedFromNotification() {
        reminderInfoViewModel.reminderLocation.value  = reminder1.location
        assertEquals(reminder1.location,reminderInfoViewModel.reminderLocation.value)
    }

    @Test
    fun displayReminder() {
        reminderInfoViewModel.displayReminder(reminder1)
        assertEquals(reminder1.title,reminderInfoViewModel.reminderTitle.value)
        assertEquals(reminder1.description,reminderInfoViewModel.reminderDescription.value)
        assertEquals(reminder1.location,reminderInfoViewModel.reminderLocation.value)

    }
}