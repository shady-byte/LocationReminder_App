package com.udacity.project4.data.localDataSource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemindersDao {
//add a reminder to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createReminder(reminder: ReminderData): Long

//get all reminders
    @Query("SELECT * FROM reminders_data_table")
    fun getAllReminders(): LiveData<List<ReminderData>>

//get a reminder with id
    @Query("SELECT * FROM reminders_data_table WHERE reminder_id=:id")
    suspend fun getReminderWithId(id:Long): ReminderData

//delete a reminder from database
    @Query("DELETE FROM reminders_data_table WHERE reminder_id == :reminderId")
    suspend fun deleteReminder(reminderId: Long)

//clear database
    @Query("DELETE FROM reminders_data_table")
    suspend fun clearDataBase()


}