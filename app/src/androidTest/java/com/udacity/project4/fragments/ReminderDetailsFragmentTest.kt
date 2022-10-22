package com.udacity.project4.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.udacity.project4.R
import com.udacity.project4.ui.ReminderDetailsFragment
import com.udacity.project4.ui.ReminderDetailsFragmentDirections
import com.udacity.project4.utils.appModule
import com.udacity.project4.viewModels.SaveReminderViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.java.KoinJavaComponent.getKoin
import org.mockito.Mockito


class ReminderDetailsFragmentTest {
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun initViewModel() {
        viewModel = getKoin().get<SaveReminderViewModel>()
    }

    @After
    fun reset() {
        unloadKoinModules(appModule)
        loadKoinModules(appModule)
    }

    @Test
    fun createReminderWithoutAllDataFieldsFilled_snackBarWillAppear() {
        //GIVEN
        val activityScenario = launchFragmentInContainer<ReminderDetailsFragment>(
            null,
            R.style.Theme_LocationReminderApp
        )
        //WHEN
        onView(withId(R.id.reminder_title_field)).perform(replaceText("Reminder 1"))
        onView(withId(R.id.reminder_description_field)).perform(replaceText("Description"))
        onView(withId(R.id.details_fab)).perform(click())

        //THEN
        onView(withText(R.string.data_fields_empty))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }
    @Test
    fun createReminderAddItToViewModel_navigateToRemindersListFragment() {
        val activityScenario = launchFragmentInContainer<ReminderDetailsFragment>(null, R.style.Theme_LocationReminderApp)
        val navController  = Mockito.mock(NavController::class.java)
        activityScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        //WHEN
        viewModel.reminderTitle.postValue("Reminder 1")
        viewModel.reminderDescription.postValue("Description")
        viewModel.reminderLocation.postValue("Alexandria,Egypt")
        viewModel.reminderLatitude.postValue(95.1)
        viewModel.reminderLongitude.postValue(2.5)

        onView(withId(R.id.details_fab)).perform(click())

        //THEN
        Mockito.verify(navController).navigateUp()

        activityScenario.close()

    }


    @Test
    fun clickLocationView_navigatesToMapFragment() {
        //GIVEN
        val activityScenario = launchFragmentInContainer<ReminderDetailsFragment>(null, R.style.Theme_LocationReminderApp)
        val navController  = Mockito.mock(NavController::class.java)
        activityScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN
        onView(withId(R.id.location_view)).perform(click())

        //THEN
        Mockito.verify(navController).navigate(ReminderDetailsFragmentDirections.actionReminderDetailsFragment2ToMapsFragment())

        activityScenario.close()
    }
}

