package com.udacity.project4.ui

import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderInfoBinding
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.utils.NOTIFICATION_ID
import com.udacity.project4.utils.REMINDER_NOW
import com.udacity.project4.utils.cancelNotification
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderInfoBinding
    private val myViewModel by viewModel<ReminderInfoActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reminder_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.home_title)
        val reminderData = intent?.extras?.get(REMINDER_NOW) as ReminderData
        val notificationId = intent?.extras?.get(NOTIFICATION_ID) as Int

        myViewModel.displayReminder(reminderData)
        binding.viewModel = myViewModel
        binding.lifecycleOwner = this

        //remove notification
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotification(notificationId)//
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { onBackPressed() }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}