package com.udacity.project4.data.localDataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ReminderData::class], version = 10, exportSchema = false)
abstract class RemindersDataBase: RoomDatabase() {
    abstract val reminderDao: RemindersDao

    companion object {
        @Volatile
        private var INSTANCE: RemindersDataBase?=null

        fun getInstance(context: Context) : RemindersDataBase {
            synchronized(lock = this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RemindersDataBase::class.java, "reminders_info_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}