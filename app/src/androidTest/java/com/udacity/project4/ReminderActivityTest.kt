package com.udacity.project4

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import com.udacity.project4.ui.MainActivity
import com.udacity.project4.utils.appModule
import com.udacity.project4.viewModels.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.java.KoinJavaComponent.getKoin


@ExperimentalCoroutinesApi
@LargeTest
class ReminderActivityTest {
    private lateinit var repository: ReminderRepositoryDataSource
    private lateinit var saveReminderViewModel : SaveReminderViewModel

    @Before
    fun init() {
        repository = getKoin().get<ReminderRepositoryDataSource>()
        saveReminderViewModel = getKoin().get<SaveReminderViewModel>()
        runBlocking {
            repository.clearReminders()
        }
    }

    @After
    fun reset() {
        unloadKoinModules(appModule)
        loadKoinModules(appModule)
    }

    @Test
    fun editReminder() = runBlocking {
        //GIVEN
        val reminder =ReminderData("Reminder 1","one task to do","Alexandria",
            4.1,5.8,2L)
        // Set initial state.
        repository.addReminder(reminder)

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // WHEN check the list and verify that all the data is correct.
        onView(withText("Reminder 1")).perform(click())
        onView(withId(R.id.reminder_title_view)).check(matches(withText("Reminder 1")))
        onView(withId(R.id.reminder_description_view)).check(matches(withText("one task to do")))
        onView(withId(R.id.reminder_location_view)).check(matches(withText("Alexandria")))

        // click the fab and create new one
        onView(withId(R.id.list_fab)).perform(click())
        onView(withId(R.id.reminder_title_field)).perform(replaceText("new Reminder"))
        onView(withId(R.id.reminder_description_field)).perform(replaceText("new Description"))
        onView(withId(R.id.map_location)).perform(setTextInTextView("new Location"))
        onView(withId(R.id.details_fab)).perform(click())
        onView(withText("new Reminder")).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun addReminder_toastMessageWillBeDisplayed() = runBlocking {
        //GIVEN
        val reminder = ReminderData("Reminder 1","one task to do","Alexandria",
            4.1,5.8,2L)

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_fab)).perform(click())
            //FragmentScenario.launch(ReminderDetailsFragment::class.java)
            //

        // WHEN add the reminder to viewModel
        saveReminderViewModel.reminderTitle.postValue(reminder.title)
        saveReminderViewModel.reminderDescription.postValue(reminder.description)
        saveReminderViewModel.reminderLatitude.postValue(reminder.latitude)
        saveReminderViewModel.reminderLongitude.postValue(reminder.longitude)
        saveReminderViewModel.reminderLocation.postValue(reminder.location)
        onView(withId(R.id.details_fab)).perform(click())

        // click the fab and create new one and navigate to Reminder list fragment
        //and check the toast is message is displayed
        onView(withText(R.string.reminder_is_added)).inRoot(
            withDecorView(
                not(`is`(getActivity(null)?.window?.decorView))
            )
        ).check(matches(isDisplayed()))

        activityScenario.close()
    }

    private fun setTextInTextView(value: String?): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
            }

            override fun perform(uiController: UiController?, view: View) {
                (view as TextView).text = value
            }

            override fun getDescription(): String {
                return "replace text"
            }
        }
    }
}