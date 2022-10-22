package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.adapters.Result
import com.udacity.project4.adapters.getOrAwaitValue
import com.udacity.project4.data.localDataSource.LocalDataSource
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.localDataSource.RemindersDataBase
import com.udacity.project4.data.repositories.ReminderDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    private lateinit var localSource: ReminderDataSource
    private lateinit var database: RemindersDataBase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
        RemindersDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        localSource = LocalDataSource(database.reminderDao)
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun insertReminder_getItBack() = runBlocking {
        val reminder = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        //insert data to database
        localSource.addReminder(reminder)

        //get reminder with Id
        val retrieved = localSource.getReminder(reminder.reminder_id)

        assertThat<ReminderData>((retrieved as Result.Success).data, `is`(notNullValue()))
        assertThat<ReminderData>((retrieved as Result.Success).data,`is`(reminder))
    }

    @Test
    fun insertReminders_deleteIt() = runBlocking {
        val reminder = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        //insert data to database
        localSource.addReminder(reminder)

        //delete reminder
        localSource.deleteReminder(reminder.reminder_id)

        //get Reminder with id
        val retrieved = localSource.getReminder(reminder.reminder_id)
        assertThat<String>((retrieved as Result.Error).message,
            containsString("error with getting a reminder"))
    }

    @Test
    fun insertReminders_getAllOfThem() = runBlocking{
        val reminder1 = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        val reminder2 = ReminderData("Reminder 2","two task to do","Classic, Alexandria",
            56.7,12.5,4L)
        //insert data to database
        localSource.addReminder(reminder1)
        localSource.addReminder(reminder2)

        //get all reminder in database
        val retrieved = localSource.getReminders()

        assertThat<List<ReminderData>>((retrieved.getOrAwaitValue() as Result.Success).data,
            `is`(listOf(reminder1,reminder2)))
    }

    @Test
    fun insertReminders_clearAllOfThem() = runBlocking {
        val reminder1 = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        val reminder2 = ReminderData("Reminder 2","two task to do","Classic, Alexandria",
            56.7,12.5,4L)
        //insert data to database
        localSource.addReminder(reminder1)
        localSource.addReminder(reminder2)

        //delete all reminders
        localSource.clearReminders()

        //get All reminders from database
        val retrieved = localSource.getReminders()

        assertThat<List<ReminderData>>((retrieved.getOrAwaitValue() as Result.Success).data,
            `is`(listOf()))
    }
}