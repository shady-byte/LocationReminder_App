package com.udacity.project4.data.localDataSource

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Reminders_data_table")
data class  ReminderData(
    @ColumnInfo(name = "title_column")
    val title: String,

    @ColumnInfo(name = "description_column")
    val description: String?=null,

    @ColumnInfo(name = "location_column")
    val location: String?=null,

    @ColumnInfo(name = "latitude_column")
    val latitude: Double?=null,

    @ColumnInfo(name = "longitude_column")
    val longitude: Double?=null,

    @PrimaryKey(autoGenerate = true)
    var reminder_id: Long = 0
): Parcelable {}
