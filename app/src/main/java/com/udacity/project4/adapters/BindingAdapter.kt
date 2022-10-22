package com.udacity.project4.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.data.localDataSource.ReminderData


@BindingAdapter("ReminderListAdapter")
fun RecyclerView.bindRecyclerView(result: Result<MutableList<ReminderData>>?) {
    result?.let {
        if(it is Result.Success<MutableList<ReminderData>>) {
            val adapter = this.adapter  as ReminderListAdapter
            adapter.submitList(it.data)
        }
    }
}
