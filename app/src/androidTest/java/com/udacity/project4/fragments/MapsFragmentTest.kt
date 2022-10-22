package com.udacity.project4.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.udacity.project4.R
import com.udacity.project4.ui.MapsFragment
import org.junit.Test
import org.mockito.Mockito

class MapsFragmentTest {

    @Test
    fun openGoogleMap_PressSaveButton_NavigateBackToDetailsFragment() {
        //GIVEN
        val activityScenario = launchFragmentInContainer<MapsFragment>(null, R.style.Theme_LocationReminderApp)
        val navController  = Mockito.mock(NavController::class.java)
        activityScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN
        onView(withContentDescription("Google Map")).perform(click())
        onView(withId(R.id.save_button)).perform(click())

        //THEN
        Mockito.verify(navController).popBackStack()

        activityScenario.close()
    }
}