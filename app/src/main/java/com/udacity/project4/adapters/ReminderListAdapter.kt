package com.udacity.project4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.databinding.ReminderItemLayoutBinding
import com.udacity.project4.data.localDataSource.ReminderData

class ReminderListAdapter(): ListAdapter<ReminderData,ReminderListAdapter.ViewHolder>(ReminderCallBack) {

    companion object ReminderCallBack: DiffUtil.ItemCallback<ReminderData>() {
        override fun areItemsTheSame(oldItem: ReminderData, newItem: ReminderData): Boolean {
            return oldItem.reminder_id == newItem.reminder_id
        }

        override fun areContentsTheSame(oldItem: ReminderData, newItem: ReminderData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ReminderItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: ReminderData) {
            binding.reminder = reminder
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ReminderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }
}