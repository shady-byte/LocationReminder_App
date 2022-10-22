package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.adapters.getOrAwaitValue
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.localDataSource.RemindersDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class ReminderDaoTest {

    private lateinit var database: RemindersDataBase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
        RemindersDataBase::class.java).build()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun insertReminders_getItById() = runBlocking {
        val reminder = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        //insert reminder to database
        database.reminderDao.createReminder(reminder)

        //get reminder from database by id
        val retrieved = database.reminderDao.getReminderWithId(reminder.reminder_id)

        assertThat<ReminderData>(retrieved as ReminderData, notNullValue())
        assertThat<String>(retrieved.title, `is`(reminder.title))
    }

    @Test
    fun insertReminders_DeleteIt_getEmptyDatabase() =  runBlocking {
        val reminder = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        //insert reminder to database
        database.reminderDao.createReminder(reminder)

        //delete it from database
        database.reminderDao.deleteReminder(reminder.reminder_id)

        //get reminder from database
        val retrieved = database.reminderDao.getReminderWithId(reminder.reminder_id)

        assertThat<ReminderData>(retrieved, `is`(nullValue()))
    }

    @Test
    fun insertReminders_getThemBack() = runBlocking {
        val reminder1 = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        val reminder2 = ReminderData("Reminder 2","two task to do","Classic, Cairo",
            6.4,723.34,5L)
        //insert reminder to database
        database.reminderDao.createReminder(reminder1)
        database.reminderDao.createReminder(reminder2)

        //get reminders from database
        val retrieved = database.reminderDao.getAllReminders()

        assertThat<List<ReminderData>>(retrieved.getOrAwaitValue(), notNullValue())
        assertThat<List<ReminderData>>(retrieved.getOrAwaitValue(), `is`(listOf(reminder1,reminder2)))
    }

    @Test
    fun insertReminders_clearThem() = runBlocking {
        val reminder1 = ReminderData("Reminder 1","one task to do","Abo noura, Alexandria",
            5.1,76.4,2L)
        val reminder2 = ReminderData("Reminder 2","two task to do","Classic, Cairo",
            6.4,723.34,5L)
        //insert reminder to database
        database.reminderDao.createReminder(reminder1)
        database.reminderDao.createReminder(reminder2)

        //delete them from database
        database.reminderDao.clearDataBase()

        //get them to make sure they are deleted
        val retrieved = database.reminderDao.getAllReminders()

        assertThat<List<ReminderData>>(retrieved.getOrAwaitValue(), notNullValue())
        assertThat<List<ReminderData>>(retrieved.getOrAwaitValue(),`is`(listOf()))
    }
}
