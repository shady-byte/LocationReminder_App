package com.udacity.project4.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.source.FakeReminderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import com.udacity.project4.adapters.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

internal class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeRepository: FakeReminderRepository

    private val reminder1 = ReminderData("reminder 1","one task today","Abo Noura,Alexandria",3.0,5.2,54L)
    private val reminder2 = ReminderData("reminder 2","Two task today","Sawarikh,Alexandria",6.1,8.3)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var testDispatchers = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createViewModel() {
       fakeRepository = FakeReminderRepository()
       saveReminderViewModel = SaveReminderViewModel(fakeRepository)
    }

    @Test
    fun getReminderTitle_setsReminderTitleAndGetIt() {
        saveReminderViewModel.reminderTitle.value = reminder1.title
        assertEquals(reminder1.title,saveReminderViewModel.reminderTitle.value)
    }

    @Test
    fun getReminderDescription_setsReminderDescriptionAndGetIt() {
        saveReminderViewModel.reminderDescription.value = reminder1.description
        assertEquals(reminder1.description,saveReminderViewModel.reminderDescription.value)
    }

    @Test
    fun getReminderLocation_setsReminderLocationAndGetIt() {
        saveReminderViewModel.reminderLocation.value = reminder1.location
        assertEquals(reminder1.location,saveReminderViewModel.reminderLocation.value)
    }

    @Test
    fun getReminderLatitude_setsReminderLatitudeAndGetIt() {
        saveReminderViewModel.reminderLatitude.value = reminder1.latitude
        assertEquals(reminder1.latitude,saveReminderViewModel.reminderLatitude.value)
    }

    @Test
    fun getReminderLongitude_setsReminderLongitudeAndGetIt() {
        saveReminderViewModel.reminderLongitude.value = reminder1.longitude
        assertEquals(reminder1.longitude,saveReminderViewModel.reminderLongitude.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addReminder_addNewReminderForLocalDataSourceAndRetrieveRemindersList() = runTest {
        saveReminderViewModel.reminderTitle.value = reminder2.title
        saveReminderViewModel.reminderDescription.value = reminder2.description
        saveReminderViewModel.reminderLocation.value = reminder2.location
        saveReminderViewModel.reminderLatitude.value = reminder2.latitude
        saveReminderViewModel.reminderLongitude.value = reminder2.longitude
        saveReminderViewModel.addReminder()
        val reminderList = saveReminderViewModel.remindersList
        advanceUntilIdle()
        assertEquals(listOf(reminder2),(reminderList.getOrAwaitValue() as Result.Success).data)
    }

    @Test
    fun clearFields_clearAllLiveData() {
        saveReminderViewModel.reminderTitle.value = reminder1.title
        saveReminderViewModel.reminderDescription.value = reminder1.description
        saveReminderViewModel.reminderLocation.value = reminder1.location
        saveReminderViewModel.reminderLongitude.value = reminder1.longitude
        saveReminderViewModel.reminderLatitude.value = reminder1.latitude

        saveReminderViewModel.clearFields()

        assertEquals("",saveReminderViewModel.reminderTitle.value)
        assertEquals("",saveReminderViewModel.reminderDescription.value)
        assertEquals("",saveReminderViewModel.reminderLocation.value)
        assertEquals(0.0,saveReminderViewModel.reminderLongitude.value)
        assertEquals(0.0,saveReminderViewModel.reminderLatitude.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkLoading_onnAddingNewReminder() = runTest {
        saveReminderViewModel.reminderTitle.value = reminder2.title
        saveReminderViewModel.reminderDescription.value = reminder2.description
        saveReminderViewModel.reminderLocation.value = reminder2.location
        saveReminderViewModel.reminderLatitude.value = reminder2.latitude
        saveReminderViewModel.reminderLongitude.value = reminder2.longitude
        saveReminderViewModel.addReminder()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        advanceUntilIdle()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }


}