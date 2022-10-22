package com.udacity.project4.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.adapters.Result
import com.udacity.project4.adapters.getOrAwaitValue
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.source.FakeReminderRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import kotlin.test.assertEquals

class RemindersListViewModelTest {

    private lateinit var reminderListViewModel: RemindersListViewModel
    private lateinit var fakeRepository: FakeReminderRepository

    private val reminder1 = ReminderData("reminder 1","one task today","Abo Noura,Alexandria",3.0,5.2,54L)
    private val reminder2 = ReminderData("reminder 2","Two task today","Sawarikh,Alexandria",6.1,8.3,12L)
    private val reminder3 = ReminderData("reminder 3","Three task today","Bibliotheque,Alexandria",13.0,61.2,72L)

    //to test function returns live data
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //to test function which runs viewModelSCope
    @ExperimentalCoroutinesApi
    @get:Rule
    var testDispatcher =  MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createViewModel() {
        fakeRepository = FakeReminderRepository()
        fakeRepository.addReminders(reminder1,reminder2,reminder3)
        reminderListViewModel = RemindersListViewModel(fakeRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getRemindersList_getListOfRemindersFromLocalDataSource() = runTest {
        val reminderList = reminderListViewModel.remindersList
        advanceUntilIdle()
        assertEquals(listOf(reminder1,reminder2,reminder3),(reminderList.getOrAwaitValue() as Result.Success).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun clearReminders_removeAllRemindersFromLocalDataSource() = runTest {
        reminderListViewModel.clearReminders()
        val reminderList = reminderListViewModel.remindersList
        advanceUntilIdle()
        assertEquals(listOf(),(reminderList.value as Result.Success<List<ReminderData>>).data)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun errorArisesWhenNODataFetchedFromDataSource_callErrorToDisplay() = runTest {
        fakeRepository.setReturnError(true)
        fakeRepository.addReminder(reminder1)
        assertEquals(true,reminderListViewModel.error.getOrAwaitValue())
    }

}