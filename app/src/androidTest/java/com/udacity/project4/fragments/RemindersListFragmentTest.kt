package com.udacity.project4.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.udacity.project4.R
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import com.udacity.project4.ui.RemindersListFragment
import com.udacity.project4.ui.RemindersListFragmentDirections
import com.udacity.project4.utils.appModule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.java.KoinJavaComponent
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class RemindersListFragmentTest {


    private lateinit var repository: ReminderRepositoryDataSource

    @Before
    fun initRepository() {
        repository = KoinJavaComponent.getKoin().get<ReminderRepositoryDataSource>()
        runBlocking {
            repository.clearReminders()
        }
    }

    @After
    fun reset() {
        runBlocking {
            unloadKoinModules(appModule)
            loadKoinModules(appModule)
        }
    }


    @Test
    fun createReminder_checkItShouldBeDisplayed(): Unit =  runBlocking {
        //GIVEN  the reminders are given to the repository
        val reminder1 = ReminderData("Reminder 1","Description 1","Alexandria",
            4.1,3.2,2L)
        repository.addReminder(reminder1)

        launchFragmentInContainer<RemindersListFragment>(null,R.style.Theme_LocationReminderApp)

        //THEN we check of the reminder is saved and displayed or not
        onView(withId(R.id.reminder_title_view)).check(matches(withText("Reminder 1")))
        onView(withId(R.id.reminder_description_view)).check(matches(withText("Description 1")))
        onView(withId(R.id.reminder_location_view)).check(matches(withText("Alexandria")))
    }

    @Test
    fun clickFab_navigateToAddNewReminderFragment() {
        //GIVEN
        val scenario = launchFragmentInContainer<RemindersListFragment>(null, R.style.Theme_LocationReminderApp)
        val navController  = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN clicking the location button
        onView(withId(R.id.list_fab)).perform(click())

        //THEN we check that it navigates successfully to MapFragment
        verify(navController).navigate(RemindersListFragmentDirections.actionRemindersListFragmentToReminderDetailsFragment2())
    }

    @Test
    fun clickLogOutButton_checkItNavigatesToHomeScreen() {
        //GIVEN
        val scenario = launchFragmentInContainer<RemindersListFragment>(null, R.style.Theme_LocationReminderApp)
        val navController  = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        //WHEN
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_item)).perform(click())
    }
}