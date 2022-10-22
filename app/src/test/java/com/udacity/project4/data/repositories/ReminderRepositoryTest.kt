package com.udacity.project4.data.repositories

import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.source.FakeLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

//internal
class ReminderRepositoryTest {
    private val reminder1 = ReminderData("reminder 1","one task today","Abo Noura,Alexandria",3.0,5.2,54L)
    private val reminder2 = ReminderData("reminder 2","Two task today","Sawarikh,Alexandria",6.1,8.3,12L)
    private val newReminder = ReminderData("reminder 3","Three task today","Bibliotheque,Alexandria",13.0,61.2,72L)
    private val localData = listOf<ReminderData>(reminder1,reminder2)

    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var reminderRepository: ReminderRepository

    @Before
    fun createRepository() {
        localDataSource = FakeLocalDataSource(localData.toMutableList())
        reminderRepository = ReminderRepository(localDataSource)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getReminders_requestAllRemindersFromLocalDataSource() = runBlocking {
        val reminders = reminderRepository.getReminders()
        assertEquals(localData,(reminders.value as Result.Success).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getReminder_getReminderWithIdFromDataSource() = runBlocking {
        val reminder = reminderRepository.getReminder(reminder2.reminder_id)
        assertEquals(reminder2,(reminder as Result.Success).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteReminder_removesReminderFromLocalDataSource() = runBlocking {
        reminderRepository.deleteReminder(reminder2.reminder_id)
        val reminders = reminderRepository.getReminders()
        assertEquals(listOf(reminder1),(reminders.value as Result.Success).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteReminders_removeAllRemindersFromLocalDataSource()= runBlocking {
        reminderRepository.clearReminders()
        val reminders = reminderRepository.getReminders()
        assertEquals(listOf<ReminderData>(),(reminders.value as Result.Success).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addReminder_addReminderToLocalDataSource()= runBlocking {
        reminderRepository.addReminder(newReminder)
        val reminders = reminderRepository.getReminders()
        val list = listOf<ReminderData>(reminder1,reminder2,newReminder)
        assertEquals(list,(reminders.value as Result.Success).data)
    }

}