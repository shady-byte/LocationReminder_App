package com.udacity.project4.utils

import com.udacity.project4.data.localDataSource.LocalDataSource
import com.udacity.project4.data.localDataSource.RemindersDataBase
import com.udacity.project4.data.repositories.ReminderDataSource
import com.udacity.project4.data.repositories.ReminderRepository
import com.udacity.project4.data.repositories.ReminderRepositoryDataSource
import com.udacity.project4.viewModels.MainActivityViewModel
import com.udacity.project4.ui.ReminderInfoActivityViewModel
import com.udacity.project4.viewModels.RemindersListViewModel
import com.udacity.project4.viewModels.SaveReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //instance of reminder database dao
    single {
        RemindersDataBase.getInstance(get()).reminderDao
    }
    //instance of local data source
    single<ReminderDataSource> {
        LocalDataSource(get())
    }
    //instance of reminder repository
    single<ReminderRepositoryDataSource> {
        ReminderRepository(get())
    }
    //instance of mainViewModel
    viewModel{
        MainActivityViewModel(get(),get())
    }
    //instance of saveReminderViewModel
    single{
        SaveReminderViewModel(get())
    }
    //instance of remindersListViewModel
    viewModel {
        RemindersListViewModel(get())
    }
    //instance of reminderInfoViewModel
    viewModel{
        ReminderInfoActivityViewModel()
    }
}

//single { params -> ReminderActivityViewModel(reminder = params.get()) }