package com.udacity.project4.ui

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.udacity.project4.BuildConfig
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.udacity.project4.R
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.databinding.ActivityMainBinding
import com.udacity.project4.adapters.Result
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.viewModels.*
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainActivityViewModel>()
    private val SaveReminderviewModel by viewModel<SaveReminderViewModel>()
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment_view) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.home_title)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.createChannel(getString(R.string.notification_channel_id),getString(R.string.notification_channel_name))

        //Listeners
        viewModel.remindersList.observe(this) { list ->
            list?.let { it ->
                if(it is Result.Success<List<ReminderData>>) {
                    if(it.data.isNotEmpty()) {
                        viewModel.geofenceList.clear()
                        it.data.forEach { item ->
                            viewModel.geoList(item)
                        }
                        viewModel.addGeofence()
                    }
                }
            }
        }

        //listener for user state logged in or out
        viewModel.authenticationState.observe(this) { authState ->
            when (authState) {
                MainActivityViewModel.AuthenticationState.AUTHENTICATED -> {
                    if(navController.currentDestination?.label.toString()
                        == "HomeFragment") {
                        navController
                            .navigate(HomeFragmentDirections.actionHomeFragmentToRemindersListFragment())
                    }
                }
                MainActivityViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    navController.navigate(R.id.homeFragment)
                }
                else -> {}
            }
        }
        navController.addOnDestinationChangedListener {_,destination,_ ->
            when (destination.id) {
                R.id.reminderDetailsFragment -> {
                    binding.fab.hide()
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)

                }
                R.id.remindersListFragment -> {
                    binding.fab.hide()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    binding.fab.hide()
                }
            }

        }
        //to use navigation correctly with fragment containerView
        //navController = binding.mainFragmentView.getFragment<NavHostFragment>().navController
        //NavigationUI.setupActionBarWithNavController(this,navController)
        binding.fab.hide()
    }


    override fun onSupportNavigateUp(): Boolean {
        return  navController.navigateUp()
    }
    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("ListTest", "onRequestPermissionResult")

        if (grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)) {
            Snackbar.make(
                binding.mainFragmentView,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("ListTest", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.mainFragmentView,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                viewModel.addGeofence()
                Log.d("MainTest","location permitted")
            }
        }
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else ->REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Log.d("ListTest", "Request foreground only location permission")
        ActivityCompat.requestPermissions(
            this,
            permissionsArray,
            resultCode
        )
    }


    private fun checkPermissionsAndStartGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

     */
}