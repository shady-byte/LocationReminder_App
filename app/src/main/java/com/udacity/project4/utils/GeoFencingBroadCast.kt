package com.udacity.project4.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GeoFencingBroadCast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        return GeoFenceService.onEnqueueWork(context!!,intent!!)
    }
}